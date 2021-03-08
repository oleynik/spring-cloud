package ua.infinity.courses.springmicroservices.currencyconversionservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/currency-conversion")
public class CurrencyConversionController {

    @GetMapping("/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion convert(@PathVariable String from,
                                      @PathVariable String to,
                                      @PathVariable BigDecimal quantity) {
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
                currencyConversion.getEnvironment());
    }
}
