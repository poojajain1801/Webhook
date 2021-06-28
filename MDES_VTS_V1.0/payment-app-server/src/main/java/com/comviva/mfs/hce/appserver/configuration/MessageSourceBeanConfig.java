/*
 COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.

 This software is the sole property of Comviva and is protected by copyright
 law and international treaty provisions. Unauthorized reproduction or
 redistribution of this program, or any portion of it may result in severe
 civil and criminal penalties and will be prosecuted to the maximum extent
 possible under the law. Comviva reserves all rights not expressly granted.
 You may not reverse engineer, decompile, or disassemble the software, except
 and only to the extent that such activity is expressly permitted by
 applicable law notwithstanding this limitation.

 THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.configuration;

import com.comviva.mfs.hce.appserver.util.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
@Component
public class MessageSourceBeanConfig {

    @Autowired
    private Environment environment;

    @Bean
    public ExposedResourceMessageBundleSource messageSource() {
        ExposedResourceMessageBundleSource messageSource = new ExposedResourceMessageBundleSource();
        List<String> baseNames = new ArrayList<>();
        String localizationMessagePath = environment.getProperty("message.resources.path");
        String messageResourcesBaseName = environment.getProperty("spring.messages.basename", String.class, "messages");
        Integer messageResourcesCacheSeconds = environment.getProperty("spring.messages.cache-seconds", Integer.class, -1);
        if (StringUtils.isNotBlank(localizationMessagePath)) {
            baseNames.add(String.format("file:%s", Paths.get(localizationMessagePath, messageResourcesBaseName)));
        }
        baseNames.add("classpath:" + messageResourcesBaseName);
        messageSource.setBasenames(baseNames.toArray(new String[baseNames.size()]));
        messageSource.setCacheSeconds(messageResourcesCacheSeconds);
        messageSource.setDefaultEncoding(Constants.DEFAULT_ENCODING.getValue());
        return messageSource;
    }

}
