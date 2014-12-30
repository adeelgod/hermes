package com.m11n.hermes.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;
import org.joda.beans.*;
import org.joda.beans.impl.direct.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import org.joda.beans.Bean;
import org.joda.beans.BeanBuilder;
import org.joda.beans.BeanDefinition;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectBean;
import org.joda.beans.impl.direct.DirectBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

@BeanDefinition
@JsonIgnoreProperties({"meta", "metaBean"})
@XmlRootElement(name = "bank_statement_pattern")
@Entity
@Table(name = "bank_statement_pattern")
public class BankStatementPattern extends DirectBean
{
    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true)
    private String id;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "name", unique = true)
    private String name;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "pos")
    private Integer pos;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "pattern", length = 2048)
    private String pattern;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "case_insensitive")
    private Boolean caseInsensitive;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "pattern_group")
    private Integer patternGroup;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "attribute")
    private String attribute;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "stop_on_first_match")
    private Boolean stopOnFirstMatch;

    @PropertyDefinition
    @Transient
    private Pattern regex;

    public BankStatementPattern() {

    }

    public BankStatementPattern(String name, Integer pos, String pattern, Boolean caseInsensitive, Integer patternGroup, String attribute, Boolean stopOnFirstMatch) {
        this.name = name;
        this.pos = pos;
        this.pattern = pattern;
        this.caseInsensitive = caseInsensitive;
        this.patternGroup = patternGroup;
        this.attribute = attribute;
        this.stopOnFirstMatch = stopOnFirstMatch;
    }

    //------------------------- AUTOGENERATED START -------------------------
    ///CLOVER:OFF
    /**
     * The meta-bean for {@code BankStatementPattern}.
     * @return the meta-bean, not null
     */
    public static BankStatementPattern.Meta meta() {
        return BankStatementPattern.Meta.INSTANCE;
    }

    static {
        JodaBeanUtils.registerMetaBean(BankStatementPattern.Meta.INSTANCE);
    }

    @Override
    public BankStatementPattern.Meta metaBean() {
        return BankStatementPattern.Meta.INSTANCE;
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the id.
     * @return the value of the property
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id  the new value of the property
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the the {@code id} property.
     * @return the property, not null
     */
    public final Property<String> id() {
        return metaBean().id().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the name.
     * @return the value of the property
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name  the new value of the property
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the the {@code name} property.
     * @return the property, not null
     */
    public final Property<String> name() {
        return metaBean().name().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the pos.
     * @return the value of the property
     */
    public Integer getPos() {
        return pos;
    }

    /**
     * Sets the pos.
     * @param pos  the new value of the property
     */
    public void setPos(Integer pos) {
        this.pos = pos;
    }

    /**
     * Gets the the {@code pos} property.
     * @return the property, not null
     */
    public final Property<Integer> pos() {
        return metaBean().pos().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the pattern.
     * @return the value of the property
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the pattern.
     * @param pattern  the new value of the property
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    /**
     * Gets the the {@code pattern} property.
     * @return the property, not null
     */
    public final Property<String> pattern() {
        return metaBean().pattern().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the caseInsensitive.
     * @return the value of the property
     */
    public Boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    /**
     * Sets the caseInsensitive.
     * @param caseInsensitive  the new value of the property
     */
    public void setCaseInsensitive(Boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }

    /**
     * Gets the the {@code caseInsensitive} property.
     * @return the property, not null
     */
    public final Property<Boolean> caseInsensitive() {
        return metaBean().caseInsensitive().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the patternGroup.
     * @return the value of the property
     */
    public Integer getPatternGroup() {
        return patternGroup;
    }

    /**
     * Sets the patternGroup.
     * @param patternGroup  the new value of the property
     */
    public void setPatternGroup(Integer patternGroup) {
        this.patternGroup = patternGroup;
    }

    /**
     * Gets the the {@code patternGroup} property.
     * @return the property, not null
     */
    public final Property<Integer> patternGroup() {
        return metaBean().patternGroup().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the attribute.
     * @return the value of the property
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Sets the attribute.
     * @param attribute  the new value of the property
     */
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    /**
     * Gets the the {@code attribute} property.
     * @return the property, not null
     */
    public final Property<String> attribute() {
        return metaBean().attribute().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the stopOnFirstMatch.
     * @return the value of the property
     */
    public Boolean getStopOnFirstMatch() {
        return stopOnFirstMatch;
    }

    /**
     * Sets the stopOnFirstMatch.
     * @param stopOnFirstMatch  the new value of the property
     */
    public void setStopOnFirstMatch(Boolean stopOnFirstMatch) {
        this.stopOnFirstMatch = stopOnFirstMatch;
    }

    /**
     * Gets the the {@code stopOnFirstMatch} property.
     * @return the property, not null
     */
    public final Property<Boolean> stopOnFirstMatch() {
        return metaBean().stopOnFirstMatch().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the regex.
     * @return the value of the property
     */
    public Pattern getRegex() {
        return regex;
    }

    /**
     * Sets the regex.
     * @param regex  the new value of the property
     */
    public void setRegex(Pattern regex) {
        this.regex = regex;
    }

    /**
     * Gets the the {@code regex} property.
     * @return the property, not null
     */
    public final Property<Pattern> regex() {
        return metaBean().regex().createProperty(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public BankStatementPattern clone() {
        return JodaBeanUtils.cloneAlways(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            BankStatementPattern other = (BankStatementPattern) obj;
            return JodaBeanUtils.equal(getId(), other.getId()) &&
                    JodaBeanUtils.equal(getName(), other.getName()) &&
                    JodaBeanUtils.equal(getPos(), other.getPos()) &&
                    JodaBeanUtils.equal(getPattern(), other.getPattern()) &&
                    JodaBeanUtils.equal(getCaseInsensitive(), other.getCaseInsensitive()) &&
                    JodaBeanUtils.equal(getPatternGroup(), other.getPatternGroup()) &&
                    JodaBeanUtils.equal(getAttribute(), other.getAttribute()) &&
                    JodaBeanUtils.equal(getStopOnFirstMatch(), other.getStopOnFirstMatch()) &&
                    JodaBeanUtils.equal(getRegex(), other.getRegex());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash = hash * 31 + JodaBeanUtils.hashCode(getId());
        hash = hash * 31 + JodaBeanUtils.hashCode(getName());
        hash = hash * 31 + JodaBeanUtils.hashCode(getPos());
        hash = hash * 31 + JodaBeanUtils.hashCode(getPattern());
        hash = hash * 31 + JodaBeanUtils.hashCode(getCaseInsensitive());
        hash = hash * 31 + JodaBeanUtils.hashCode(getPatternGroup());
        hash = hash * 31 + JodaBeanUtils.hashCode(getAttribute());
        hash = hash * 31 + JodaBeanUtils.hashCode(getStopOnFirstMatch());
        hash = hash * 31 + JodaBeanUtils.hashCode(getRegex());
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(320);
        buf.append("BankStatementPattern{");
        int len = buf.length();
        toString(buf);
        if (buf.length() > len) {
            buf.setLength(buf.length() - 2);
        }
        buf.append('}');
        return buf.toString();
    }

    protected void toString(StringBuilder buf) {
        buf.append("id").append('=').append(JodaBeanUtils.toString(getId())).append(',').append(' ');
        buf.append("name").append('=').append(JodaBeanUtils.toString(getName())).append(',').append(' ');
        buf.append("pos").append('=').append(JodaBeanUtils.toString(getPos())).append(',').append(' ');
        buf.append("pattern").append('=').append(JodaBeanUtils.toString(getPattern())).append(',').append(' ');
        buf.append("caseInsensitive").append('=').append(JodaBeanUtils.toString(getCaseInsensitive())).append(',').append(' ');
        buf.append("patternGroup").append('=').append(JodaBeanUtils.toString(getPatternGroup())).append(',').append(' ');
        buf.append("attribute").append('=').append(JodaBeanUtils.toString(getAttribute())).append(',').append(' ');
        buf.append("stopOnFirstMatch").append('=').append(JodaBeanUtils.toString(getStopOnFirstMatch())).append(',').append(' ');
        buf.append("regex").append('=').append(JodaBeanUtils.toString(getRegex())).append(',').append(' ');
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-bean for {@code BankStatementPattern}.
     */
    public static class Meta extends DirectMetaBean {
        /**
         * The singleton instance of the meta-bean.
         */
        static final Meta INSTANCE = new Meta();

        /**
         * The meta-property for the {@code id} property.
         */
        private final MetaProperty<String> id = DirectMetaProperty.ofReadWrite(
                this, "id", BankStatementPattern.class, String.class);
        /**
         * The meta-property for the {@code name} property.
         */
        private final MetaProperty<String> name = DirectMetaProperty.ofReadWrite(
                this, "name", BankStatementPattern.class, String.class);
        /**
         * The meta-property for the {@code pos} property.
         */
        private final MetaProperty<Integer> pos = DirectMetaProperty.ofReadWrite(
                this, "pos", BankStatementPattern.class, Integer.class);
        /**
         * The meta-property for the {@code pattern} property.
         */
        private final MetaProperty<String> pattern = DirectMetaProperty.ofReadWrite(
                this, "pattern", BankStatementPattern.class, String.class);
        /**
         * The meta-property for the {@code caseInsensitive} property.
         */
        private final MetaProperty<Boolean> caseInsensitive = DirectMetaProperty.ofReadWrite(
                this, "caseInsensitive", BankStatementPattern.class, Boolean.class);
        /**
         * The meta-property for the {@code patternGroup} property.
         */
        private final MetaProperty<Integer> patternGroup = DirectMetaProperty.ofReadWrite(
                this, "patternGroup", BankStatementPattern.class, Integer.class);
        /**
         * The meta-property for the {@code attribute} property.
         */
        private final MetaProperty<String> attribute = DirectMetaProperty.ofReadWrite(
                this, "attribute", BankStatementPattern.class, String.class);
        /**
         * The meta-property for the {@code stopOnFirstMatch} property.
         */
        private final MetaProperty<Boolean> stopOnFirstMatch = DirectMetaProperty.ofReadWrite(
                this, "stopOnFirstMatch", BankStatementPattern.class, Boolean.class);
        /**
         * The meta-property for the {@code regex} property.
         */
        private final MetaProperty<Pattern> regex = DirectMetaProperty.ofReadWrite(
                this, "regex", BankStatementPattern.class, Pattern.class);
        /**
         * The meta-properties.
         */
        private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
                this, null,
                "id",
                "name",
                "pos",
                "pattern",
                "caseInsensitive",
                "patternGroup",
                "attribute",
                "stopOnFirstMatch",
                "regex");

        /**
         * Restricted constructor.
         */
        protected Meta() {
        }

        @Override
        protected MetaProperty<?> metaPropertyGet(String propertyName) {
            switch (propertyName.hashCode()) {
                case 3355:  // id
                    return id;
                case 3373707:  // name
                    return name;
                case 111188:  // pos
                    return pos;
                case -791090288:  // pattern
                    return pattern;
                case 190351809:  // caseInsensitive
                    return caseInsensitive;
                case -220605233:  // patternGroup
                    return patternGroup;
                case 13085340:  // attribute
                    return attribute;
                case 448597334:  // stopOnFirstMatch
                    return stopOnFirstMatch;
                case 108392519:  // regex
                    return regex;
            }
            return super.metaPropertyGet(propertyName);
        }

        @Override
        public BeanBuilder<? extends BankStatementPattern> builder() {
            return new DirectBeanBuilder<BankStatementPattern>(new BankStatementPattern());
        }

        @Override
        public Class<? extends BankStatementPattern> beanType() {
            return BankStatementPattern.class;
        }

        @Override
        public Map<String, MetaProperty<?>> metaPropertyMap() {
            return metaPropertyMap$;
        }

        //-----------------------------------------------------------------------
        /**
         * The meta-property for the {@code id} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<String> id() {
            return id;
        }

        /**
         * The meta-property for the {@code name} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<String> name() {
            return name;
        }

        /**
         * The meta-property for the {@code pos} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<Integer> pos() {
            return pos;
        }

        /**
         * The meta-property for the {@code pattern} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<String> pattern() {
            return pattern;
        }

        /**
         * The meta-property for the {@code caseInsensitive} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<Boolean> caseInsensitive() {
            return caseInsensitive;
        }

        /**
         * The meta-property for the {@code patternGroup} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<Integer> patternGroup() {
            return patternGroup;
        }

        /**
         * The meta-property for the {@code attribute} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<String> attribute() {
            return attribute;
        }

        /**
         * The meta-property for the {@code stopOnFirstMatch} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<Boolean> stopOnFirstMatch() {
            return stopOnFirstMatch;
        }

        /**
         * The meta-property for the {@code regex} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<Pattern> regex() {
            return regex;
        }

        //-----------------------------------------------------------------------
        @Override
        protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
            switch (propertyName.hashCode()) {
                case 3355:  // id
                    return ((BankStatementPattern) bean).getId();
                case 3373707:  // name
                    return ((BankStatementPattern) bean).getName();
                case 111188:  // pos
                    return ((BankStatementPattern) bean).getPos();
                case -791090288:  // pattern
                    return ((BankStatementPattern) bean).getPattern();
                case 190351809:  // caseInsensitive
                    return ((BankStatementPattern) bean).getCaseInsensitive();
                case -220605233:  // patternGroup
                    return ((BankStatementPattern) bean).getPatternGroup();
                case 13085340:  // attribute
                    return ((BankStatementPattern) bean).getAttribute();
                case 448597334:  // stopOnFirstMatch
                    return ((BankStatementPattern) bean).getStopOnFirstMatch();
                case 108392519:  // regex
                    return ((BankStatementPattern) bean).getRegex();
            }
            return super.propertyGet(bean, propertyName, quiet);
        }

        @Override
        protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
            switch (propertyName.hashCode()) {
                case 3355:  // id
                    ((BankStatementPattern) bean).setId((String) newValue);
                    return;
                case 3373707:  // name
                    ((BankStatementPattern) bean).setName((String) newValue);
                    return;
                case 111188:  // pos
                    ((BankStatementPattern) bean).setPos((Integer) newValue);
                    return;
                case -791090288:  // pattern
                    ((BankStatementPattern) bean).setPattern((String) newValue);
                    return;
                case 190351809:  // caseInsensitive
                    ((BankStatementPattern) bean).setCaseInsensitive((Boolean) newValue);
                    return;
                case -220605233:  // patternGroup
                    ((BankStatementPattern) bean).setPatternGroup((Integer) newValue);
                    return;
                case 13085340:  // attribute
                    ((BankStatementPattern) bean).setAttribute((String) newValue);
                    return;
                case 448597334:  // stopOnFirstMatch
                    ((BankStatementPattern) bean).setStopOnFirstMatch((Boolean) newValue);
                    return;
                case 108392519:  // regex
                    ((BankStatementPattern) bean).setRegex((Pattern) newValue);
                    return;
            }
            super.propertySet(bean, propertyName, newValue, quiet);
        }

    }

    ///CLOVER:ON
    //-------------------------- AUTOGENERATED END --------------------------
}
