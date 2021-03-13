package ua.infinity.courses.springmicroservices.currencyexchangeservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/currency-exchange")
public class CurrencyExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyExchangeController.class);

    @Autowired
    private Environment environment;

    @Autowired
    private CurrencyExchangeRepository repository;

    @GetMapping("/from/{from}/to/{to}")
    public CurrencyExchange exchange(@PathVariable String from, @PathVariable String to) {
        logger.info("exchange called with {} to {}", from, to);
        CurrencyExchange currencyExchange = repository.findByFromAndTo(from, to);
        if (currencyExchange == null) {
            throw new RuntimeException("Unable to find data for " + from + " to " + to);
        }
        String host = environment.getProperty("HOSTNAME");
        String version = "v0.1.3-SNAPSHOT";
        String port = environment.getProperty("local.server.port");
        currencyExchange.setEnvironment(host + ":" + port + " / " + version);
        return currencyExchange;
    }
}
