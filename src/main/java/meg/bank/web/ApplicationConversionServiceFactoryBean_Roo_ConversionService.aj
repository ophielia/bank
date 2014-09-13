// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package meg.bank.web;

import meg.bank.bus.dao.CategoryRuleDao;
import meg.bank.bus.dao.MediaUploadDao;
import meg.bank.bus.repo.CategoryRuleRepository;
import meg.bank.bus.repo.MediaUploadRepository;
import meg.bank.web.ApplicationConversionServiceFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    declare @type: ApplicationConversionServiceFactoryBean: @Configurable;
    
    @Autowired
    CategoryRuleRepository ApplicationConversionServiceFactoryBean.categoryRuleRepository;
    
    @Autowired
    MediaUploadRepository ApplicationConversionServiceFactoryBean.mediaUploadRepository;
    
    public Converter<CategoryRuleDao, String> ApplicationConversionServiceFactoryBean.getCategoryRuleDaoToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<meg.bank.bus.dao.CategoryRuleDao, java.lang.String>() {
            public String convert(CategoryRuleDao categoryRuleDao) {
                return new StringBuilder().append(categoryRuleDao.getLineorder()).append(' ').append(categoryRuleDao.getContaining()).append(' ').append(categoryRuleDao.getCategoryId()).append(' ').append(categoryRuleDao.getCatDisplay()).toString();
            }
        };
    }
    
    public Converter<Long, CategoryRuleDao> ApplicationConversionServiceFactoryBean.getIdToCategoryRuleDaoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, meg.bank.bus.dao.CategoryRuleDao>() {
            public meg.bank.bus.dao.CategoryRuleDao convert(java.lang.Long id) {
                return categoryRuleRepository.findOne(id);
            }
        };
    }
    
    public Converter<String, CategoryRuleDao> ApplicationConversionServiceFactoryBean.getStringToCategoryRuleDaoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, meg.bank.bus.dao.CategoryRuleDao>() {
            public meg.bank.bus.dao.CategoryRuleDao convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), CategoryRuleDao.class);
            }
        };
    }
    
    public Converter<MediaUploadDao, String> ApplicationConversionServiceFactoryBean.getMediaUploadDaoToStringConverter() {
        return new org.springframework.core.convert.converter.Converter<meg.bank.bus.dao.MediaUploadDao, java.lang.String>() {
            public String convert(MediaUploadDao mediaUploadDao) {
                return new StringBuilder().append(mediaUploadDao.getFilepath()).append(' ').append(mediaUploadDao.getContentType()).append(' ').append(mediaUploadDao.getImportClient()).toString();
            }
        };
    }
    
    public Converter<Long, MediaUploadDao> ApplicationConversionServiceFactoryBean.getIdToMediaUploadDaoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.Long, meg.bank.bus.dao.MediaUploadDao>() {
            public meg.bank.bus.dao.MediaUploadDao convert(java.lang.Long id) {
                return mediaUploadRepository.findOne(id);
            }
        };
    }
    
    public Converter<String, MediaUploadDao> ApplicationConversionServiceFactoryBean.getStringToMediaUploadDaoConverter() {
        return new org.springframework.core.convert.converter.Converter<java.lang.String, meg.bank.bus.dao.MediaUploadDao>() {
            public meg.bank.bus.dao.MediaUploadDao convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), MediaUploadDao.class);
            }
        };
    }
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getCategoryRuleDaoToStringConverter());
        registry.addConverter(getIdToCategoryRuleDaoConverter());
        registry.addConverter(getStringToCategoryRuleDaoConverter());
        registry.addConverter(getMediaUploadDaoToStringConverter());
        registry.addConverter(getIdToMediaUploadDaoConverter());
        registry.addConverter(getStringToMediaUploadDaoConverter());
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
}