package de.uni_leipzig.search_engine.backend;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import de.uni_leipzig.search_engine.backend.aspects.KafkaTopicProducer;
import de.uni_leipzig.search_engine.backend.lucene.SuggestionComponent;
import de.uni_leipzig.search_engine.events.EventTopics;
import de.uni_leipzig.search_engine.events.WebResponseEvent;
import lombok.SneakyThrows;

@SpringBootApplication
public class Application
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
	
	@Configuration
	@EnableAsync
	@EnableKafka
	@EnableSpringHttpSession
	public static class ApplicationConfiguration  extends WebMvcConfigurerAdapter
	{
		@Bean
		public CookieSerializer cookieSerializer()
		{
			DefaultCookieSerializer ret = new DefaultCookieSerializer();
			
			ret.setCookieName("client_id"); 
			ret.setCookiePath("/"); 
			ret.setCookieMaxAge(Integer.MAX_VALUE);
			
			return ret;
		}

		@Bean
		public static SessionRepository<?> sessionStore()
		{
			return new MapSessionRepository();
		}
		
		@Bean
		public static Analyzer analyzer()
		{
			return new StandardAnalyzer();
		}
		
		@Bean
		@SneakyThrows
		public static IndexReader indexReader(
			@Value("${indexDirectory:../example_indices/lips_informatik_uni_leipzig}")
			String indexDirectory)
		{	
			return DirectoryReader.open(FSDirectory.open(Paths.get(indexDirectory)));
		}
		
		@Bean
		public static IndexSearcher searcher(IndexReader indexReader)
		{
			return new IndexSearcher(indexReader);
		}
		
		@Bean
		public KafkaTopicProducer<WebResponseEvent> kafkaLoggingProducer(KafkaTemplate<String, String> kafkaTemplate)
		{
			return new KafkaTopicProducer<>(EventTopics.ALL_WEB_EVENTS_TOPIC, kafkaTemplate);
		}
		
		@Bean
		@SneakyThrows
		public SuggestionComponent suggestionComponent(
				@Value("${suggestionIndexDirectory:../example_suggestions/from_google}") String suggestionIndexDirectory)
		{
			Directory suggestionDirectory = FSDirectory.open(Paths.get(suggestionIndexDirectory));
			SuggestionComponent ret = new SuggestionComponent(suggestionDirectory);
			
			return ret;
		}
		
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry)
        {
            super.addResourceHandlers(registry);

            if (!registry.hasMappingForPattern("/presentation/**"))
            {
                registry.addResourceHandler("/presentation/**")
			.addResourceLocations("classpath:META-INF/resources/webjars/search-engine-final-presentation/1.0.0/");
            }
        }
	}
}
