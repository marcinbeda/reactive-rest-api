package pl.beda.reactive.rest.api.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Properties;

public class ConfigUtilsTest {

    @Test
    @DisplayName("Read properties.")
    void givenFileWithPropertiesWhenGetPropertiesThenReturnPropertiesSuccessfully() {

        Properties properties = ConfigUtils.getInstance().getProperties();
        Assertions.assertNotNull(properties);
        Assertions.assertEquals("TestValue1", properties.getProperty("key.property1"));
        Assertions.assertEquals("TestValue2", properties.getProperty("key.property2"));

    }

}
