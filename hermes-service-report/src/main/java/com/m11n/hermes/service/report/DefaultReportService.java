package com.m11n.hermes.service.report;

import com.m11n.hermes.core.service.ReportService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRAbstractLRUVirtualizer;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.fill.JRGzipVirtualizer;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Map;

@Service
public class DefaultReportService
implements ReportService
{
    private static final Logger logger = LoggerFactory.getLogger(DefaultReportService.class);
    
    public static final String VITUALIZER_FILE = "file";
    
    public static final String VITUALIZER_SWAP = "swap";
    
    public static final String VITUALIZER_GZIP = "gzip";

    @Value("${hermes.reporting.template.dir}")
    private String templateDir;
    
    @Value("${hermes.reporting.output.dir}")
    private String outputDir;
    
    @Value("${hermes.reporting.virtualizer.dir}")
    private String virtualizerDir;
    
    @Value("${hermes.reporting.virtualizer.type}")
    private String virtualizerType;
    
    @Inject
    private DataSource dataSource;
    
    @PostConstruct
    public void init() 
    {
        setupDirs();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void generate(String template, Map<String, Object> parameters, String format, String outputName)
    {
        if(template!=null)
        {
            JasperPrint jasperPrint = null;
            
            Connection connection = null;

            JRAbstractLRUVirtualizer virtualizer = null;
            
            if(VITUALIZER_FILE.equals(virtualizerType))
            {
                // creating the virtualizer
                virtualizer = new JRFileVirtualizer(2, virtualizerDir);
                parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            }
            else if(VITUALIZER_SWAP.equals(virtualizerType))
            {
                // creating the virtualizer
                JRSwapFile swapFile = new JRSwapFile("tmp", 1024, 1024);
                virtualizer = new JRSwapFileVirtualizer(2, swapFile, true);
                parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);    
            }
            else if(VITUALIZER_GZIP.equals(virtualizerType))
            {
                // creating the virtualizer
                virtualizer = new JRGzipVirtualizer(2);
                parameters.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);    
            }
            
            if (virtualizer != null)
            {
                virtualizer.setReadOnly(true);          
            }

            try 
            {
                connection = dataSource.getConnection();
                
                if(template.endsWith("jrxml"))
                {
                    JasperDesign jasperDesign = JRXmlLoader.load(getDir(templateDir) + template);
                    JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
                    jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
                }
                else if(template.endsWith("jasper"))
                {
                    jasperPrint = JasperFillManager.fillReport(DefaultReportService.class.getClassLoader().getResourceAsStream(template), parameters, connection);
                }
                
                File f = new File(getDir(outputDir) + outputName);
                
                if(!f.getParentFile().exists())
                {
                    f.getParentFile().mkdirs();
                }

                OutputStream os = new FileOutputStream(f);
                
                //JasperReportsUtils.render(getExporter(format), jasperPrint, os);
                Exporter exporter = getExporter(format);

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(os));
                exporter.exportReport();

                os.flush();
                os.close();

                jasperPrint = null;
            }
            catch(Exception ex)
            {
                logger.error("Could not create the report.", ex);
            }
            finally 
            {
                try 
                {
                    if(connection!=null)
                    {
                        connection.close();
                    }
                }
                catch(Throwable ex) 
                {
                    logger.error("Could not close JDBC Connection", ex);
                }
                try
                {
                    // TODO: find out if we have to do this
                    if(virtualizer != null)
                    {
                        virtualizer.cleanup();
                    }
                }
                catch(Throwable ex) 
                {
                    logger.error("Could not cleanup virtualizer", ex);
                }
            }
        }
    }
    
    private String getDir(String dir)
    {
        if(dir!=null && !dir.endsWith("/"))
        {
            dir += "/";
        }
        
        return dir;
    }
    
    private void setupDirs()
    {
        try
        {
            new File(templateDir).mkdirs();
            new File(outputDir).mkdirs();
            new File(virtualizerDir).mkdirs();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

    private Exporter getExporter(String format)
    {
        Exporter exporter = null;

        if(format.equalsIgnoreCase("pdf")) {
            exporter = new JRPdfExporter();
        } else if(format.equalsIgnoreCase("html")) {
            exporter = new HtmlExporter();
            //exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imageUrl);
            //exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN, Boolean.FALSE);
            //exporter.setParameter(JRHtmlExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.FALSE);
            //exporter.setParameter(JRHtmlExporterParameter.SIZE_UNIT, "px");
        } else if(format.equalsIgnoreCase("doc")) {
            exporter = new JRDocxExporter();
        } else if(format.equalsIgnoreCase("xls")) {
            exporter = new JRXlsxExporter();
        } else if(format.equalsIgnoreCase("txt")) {
            exporter = new JRTextExporter();
        }

        return exporter;
    }
}
