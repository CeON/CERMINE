package pl.edu.icm.cermine.configuration;

import com.google.common.base.Preconditions;

/**
 * Loader of configuration properties for {@link pl.edu.icm.cermine.ContentExtractor}
 * 
 * @author madryk
 */
public class ContentExtractorConfigRegister {

    private static final ThreadLocal<ContentExtractorConfig> INSTANCE =
            new ThreadLocal<ContentExtractorConfig>() {
        @Override
        protected ContentExtractorConfig initialValue() {
            return new ContentExtractorConfigBuilder().buildConfiguration();
        }
    };
    
    public static void set(ContentExtractorConfig config) {
        Preconditions.checkNotNull(config);
        INSTANCE.set(config);
    }
    
    public static ContentExtractorConfig get() {
        return INSTANCE.get();
    }

    public static void remove() {
        INSTANCE.remove();
    }    
    
}
