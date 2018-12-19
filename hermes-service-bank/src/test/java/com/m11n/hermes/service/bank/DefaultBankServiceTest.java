package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankMatchIcon;
import com.m11n.hermes.core.service.BankService;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
@ActiveProfiles("debug")
public class DefaultBankServiceTest {
    @Inject
    private BankService bankService;

    private static final BankMatchIcon[] bm = {
            new BankMatchIcon("l-carb-shop", "ebay_paypal", "complete", "30_30_ebay_paypal.bmp", null, null),
            new BankMatchIcon("l-carb-shop", "Shop_Rechnung", "complete", "30_30_l-carb_complete.bmp", null, null),
            new BankMatchIcon("l-carb-shop", "Shop_Rechnung", "complete_after_inkasso", "30_30_l-carb_complete_inkasso.bmp", null, null),
            new BankMatchIcon("l-carb-shop", "%", "holded", "30_30_l-carb_holded.bmp", null, null),
            new BankMatchIcon("fair-shea", "ebay_paypal", "complete", "30_30_ebay_paypal.bmp", null, null),
            new BankMatchIcon("fair-shea", "ebay_vorkasse", "pending", "30_30_ebay_vorkasse.bmp", null, null),
            new BankMatchIcon("fair-shea", "Shop_Rechnung", "complete", "30_30_l-carb_complete.bmp", null, null),
            new BankMatchIcon("fair-shea", "Shop_Rechnung", "complete_after_inkasso", "30_30_l-carb_complete_inkasso.bmp", null, null),
            new BankMatchIcon("fair-shea", "%", "holded", "30_30_l-carb_holded.bmp", null, null)
    };

    private static final List<BankMatchIcon> bankMatchIcons = Arrays.asList(bm);

    @Test
    public void shouldMatchIconAndActions() {
        // given
        String expectedShop = "l-carb-shop";
        String expectedType = "Shop_Rechnung";
        String expectedOrderStatus = "complete";

        // when
        Optional<BankMatchIcon> actual = bankService.matchIconAndActions(bankMatchIcons, expectedShop, expectedType, expectedOrderStatus);

        // then
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(expectedShop.toLowerCase(), actual.get().getShop().toLowerCase());
        Assert.assertEquals(expectedType.toLowerCase(), actual.get().getType().toLowerCase());
        Assert.assertEquals(expectedOrderStatus.toLowerCase(), actual.get().getStatus().toLowerCase());
    }

    @Test
    public void shouldMatchIconAndActionsDespiteTheCase() {
        // given
        String expectedShop = "l-carb-Shop";
        String expectedType = "shop_rechnung";
        String expectedOrderStatus = "Complete";

        // when
        Optional<BankMatchIcon> actual = bankService.matchIconAndActions(bankMatchIcons, expectedShop, expectedType, expectedOrderStatus);

        // then
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(expectedShop.toLowerCase(), actual.get().getShop().toLowerCase());
        Assert.assertEquals(expectedType.toLowerCase(), actual.get().getType().toLowerCase());
        Assert.assertEquals(expectedOrderStatus.toLowerCase(), actual.get().getStatus().toLowerCase());
    }

    @Test
    public void shouldMatchIconAndActionsWithPatternInType() {
        // given
        String expectedShop = "l-carb-Shop";
        String expectedType = "loremIpsumDolorem";
        String expectedOrderStatus = "holded";

        // when
        Optional<BankMatchIcon> actual = bankService.matchIconAndActions(bankMatchIcons, expectedShop, expectedType, expectedOrderStatus);

        // then
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals(expectedShop.toLowerCase(), actual.get().getShop().toLowerCase());
        Assert.assertEquals(expectedOrderStatus.toLowerCase(), actual.get().getStatus().toLowerCase());
    }

    @Test
    public void shouldNotMatchIconAndActions() {
        // given
        String expectedShop = "unknownStore";
        String expectedType = "Shop_Rechnung";
        String expectedOrderStatus = "complete_after_inkasso";

        // when
        Optional<BankMatchIcon> actual = bankService.matchIconAndActions(bankMatchIcons, expectedShop, expectedType, expectedOrderStatus);

        // then
        Assert.assertFalse(actual.isPresent());
    }
}