package ua.infinity.courses.springmicroservices.currencyconversionservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
public class CurrencyConversionController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyConversionController.class);

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convert(@PathVariable String from,
                                      @PathVariable String to,
                                      @PathVariable BigDecimal quantity) {
        logger.info("convert called with {} to {} with ", from, to, quantity);
        Map<String, String> urlVars = new HashMap<>();
        urlVars.put("from", from);
        urlVars.put("to", to);

        CurrencyConversion currencyConversion = new RestTemplate()
                .getForEntity("http://localhost:8000/currency-exchange/from/{from}/to/{to}",
                        CurrencyConversion.class,
                        urlVars)
                .getBody();

        BigDecimal conversionMultiple = currencyConversion.getConversionMultiple();
        return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
                conversionMultiple, quantity.multiply(conversionMultiple),
                currencyConversion.getEnvironment()+ " REST Template");
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convertFeign(@PathVariable String from,
                                      @PathVariable String to,
                                      @PathVariable BigDecimal quantity) {
        logger.info("convert called with {} to {} with ", from, to, quantity);
        CurrencyConversion currencyConversion = currencyExchangeProxy.exchange(from, to);

        BigDecimal conversionMultiple = currencyConversion.getConversionMultiple();
        return new CurrencyConversion(currencyConversion.getId(), from, to, quantity,
                conversionMultiple, quantity.multiply(conversionMultiple),
                currencyConversion.getEnvironment() + " Feign");
    }
}
