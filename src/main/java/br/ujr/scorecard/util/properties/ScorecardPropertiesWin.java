package br.ujr.scorecard.util.properties;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile({"default","win"})
@PropertySource({"classpath:scorecard-win.properties"})
public class ScorecardPropertiesWin extends AbstractScorecardProperties implements ScorecardProperties {


}
