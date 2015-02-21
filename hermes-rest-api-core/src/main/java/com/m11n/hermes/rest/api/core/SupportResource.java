package com.m11n.hermes.rest.api.core;

import com.m11n.hermes.core.util.PropertiesUtil;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/support")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class SupportResource {
    private static final Logger logger = LoggerFactory.getLogger(SupportResource.class);

    @Inject
    private JavaMailSender mailSender;

    @Inject
    private SimpleMailMessage simpleMailMessage;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendLog(Map<String, Object> options) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();

        String note = options.get("note")==null ? "- no message -" : options.get("note").toString();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(simpleMailMessage.getFrom());
            helper.setTo(simpleMailMessage.getTo());
            helper.setSubject(simpleMailMessage.getSubject());
            helper.setText(String.format(simpleMailMessage.getText(), note));

            final List<String> files = new ArrayList<>();

            if(Boolean.TRUE.equals(options.get("log"))) {
                String[] result = new File(".").list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".log");
                    }
                });

                for(String f : result) {
                    files.add(f);
                }
            }

            if(Boolean.TRUE.equals(options.get("properties"))) {
                files.add(PropertiesUtil.getHermesPropertiesPath());
                files.add(PropertiesUtil.getCommonPropertiesPath());
            }

            if(!files.isEmpty()) {
                Files.deleteIfExists(new File("attachement.zip").toPath());
                File tmp = new File("attachement.zip");
                logger.info("Temp for ZIP attachment: {}", tmp.getAbsolutePath());
                FileOutputStream os = new FileOutputStream(tmp);

                ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, os);

                for(String f : files) {
                    File entryFile = new File("./" + f);
                    if(entryFile.exists()) {
                        logger.info("Adding file: {}", entryFile.getAbsolutePath());
                        ArchiveEntry entry = aos.createArchiveEntry(entryFile, f);
                        aos.putArchiveEntry(entry);
                        IOUtils.copy(new FileInputStream(entryFile), aos);
                        aos.closeArchiveEntry();
                    } else {
                        logger.info("File not found: {}", entryFile.getAbsolutePath());
                    }
                }

                aos.finish();
                aos.close();

                logger.info("ZIP: {}", tmp.getAbsolutePath());

                FileSystemResource file = new FileSystemResource(tmp);
                helper.addAttachment("files.zip", file);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error(e.toString(), e);
        }

        return Response.ok().build();
    }
}
