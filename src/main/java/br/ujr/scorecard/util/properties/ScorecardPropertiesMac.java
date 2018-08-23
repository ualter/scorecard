package br.ujr.scorecard.util.properties;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile("mac")
@PropertySource({"classpath:scorecard-mac.properties"})
public class ScorecardPropertiesMac extends AbstractScorecardProperties implements ScorecardProperties {


}
