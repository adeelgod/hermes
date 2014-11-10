package com.m11n.hermes.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.GenericGenerator;
import org.joda.beans.*;
import org.joda.beans.impl.direct.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;
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
@XmlRootElement(name = "form_field")
@Entity
@Table(name = "hermes_form")
public class Form extends DirectBean
{
    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @javax.persistence.Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "uuid", unique = true)
    private String id;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "name")
    private String name;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @Column(name = "sql_statement", length = 4096)
    private String sqlStatement;

    @PropertyDefinition
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="form_id", referencedColumnName="uuid")
    private List<FormField> fields;

    //------------------------- AUTOGENERATED START -------------------------
    ///CLOVER:OFF
    /**
     * The meta-bean for {@code Form}.
     * @return the meta-bean, not null
     */
    public static Form.Meta meta() {
        return Form.Meta.INSTANCE;
    }

    static {
        JodaBeanUtils.registerMetaBean(Form.Meta.INSTANCE);
    }

    @Override
    public Form.Meta metaBean() {
        return Form.Meta.INSTANCE;
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
     * Gets the sqlStatement.
     * @return the value of the property
     */
    public String getSqlStatement() {
        return sqlStatement;
    }

    /**
     * Sets the sqlStatement.
     * @param sqlStatement  the new value of the property
     */
    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    /**
     * Gets the the {@code sqlStatement} property.
     * @return the property, not null
     */
    public final Property<String> sqlStatement() {
        return metaBean().sqlStatement().createProperty(this);
    }

    //-----------------------------------------------------------------------
    /**
     * Gets the fields.
     * @return the value of the property
     */
    public List<FormField> getFields() {
        return fields;
    }

    /**
     * Sets the fields.
     * @param fields  the new value of the property
     */
    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    /**
     * Gets the the {@code fields} property.
     * @return the property, not null
     */
    public final Property<List<FormField>> fields() {
        return metaBean().fields().createProperty(this);
    }

    //-----------------------------------------------------------------------
    @Override
    public Form clone() {
        return JodaBeanUtils.cloneAlways(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj != null && obj.getClass() == this.getClass()) {
            Form other = (Form) obj;
            return JodaBeanUtils.equal(getId(), other.getId()) &&
                    JodaBeanUtils.equal(getName(), other.getName()) &&
                    JodaBeanUtils.equal(getSqlStatement(), other.getSqlStatement()) &&
                    JodaBeanUtils.equal(getFields(), other.getFields());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = getClass().hashCode();
        hash += hash * 31 + JodaBeanUtils.hashCode(getId());
        hash += hash * 31 + JodaBeanUtils.hashCode(getName());
        hash += hash * 31 + JodaBeanUtils.hashCode(getSqlStatement());
        hash += hash * 31 + JodaBeanUtils.hashCode(getFields());
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder(160);
        buf.append("Form{");
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
        buf.append("sqlStatement").append('=').append(JodaBeanUtils.toString(getSqlStatement())).append(',').append(' ');
        buf.append("fields").append('=').append(JodaBeanUtils.toString(getFields())).append(',').append(' ');
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-bean for {@code Form}.
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
                this, "id", Form.class, String.class);
        /**
         * The meta-property for the {@code name} property.
         */
        private final MetaProperty<String> name = DirectMetaProperty.ofReadWrite(
                this, "name", Form.class, String.class);
        /**
         * The meta-property for the {@code sqlStatement} property.
         */
        private final MetaProperty<String> sqlStatement = DirectMetaProperty.ofReadWrite(
                this, "sqlStatement", Form.class, String.class);
        /**
         * The meta-property for the {@code fields} property.
         */
        @SuppressWarnings({"unchecked", "rawtypes" })
        private final MetaProperty<List<FormField>> fields = DirectMetaProperty.ofReadWrite(
                this, "fields", Form.class, (Class) List.class);
        /**
         * The meta-properties.
         */
        private final Map<String, MetaProperty<?>> metaPropertyMap$ = new DirectMetaPropertyMap(
                this, null,
                "id",
                "name",
                "sqlStatement",
                "fields");

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
                case 937767745:  // sqlStatement
                    return sqlStatement;
                case -1274708295:  // fields
                    return fields;
            }
            return super.metaPropertyGet(propertyName);
        }

        @Override
        public BeanBuilder<? extends Form> builder() {
            return new DirectBeanBuilder<Form>(new Form());
        }

        @Override
        public Class<? extends Form> beanType() {
            return Form.class;
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
         * The meta-property for the {@code sqlStatement} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<String> sqlStatement() {
            return sqlStatement;
        }

        /**
         * The meta-property for the {@code fields} property.
         * @return the meta-property, not null
         */
        public final MetaProperty<List<FormField>> fields() {
            return fields;
        }

        //-----------------------------------------------------------------------
        @Override
        protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
            switch (propertyName.hashCode()) {
                case 3355:  // id
                    return ((Form) bean).getId();
                case 3373707:  // name
                    return ((Form) bean).getName();
                case 937767745:  // sqlStatement
                    return ((Form) bean).getSqlStatement();
                case -1274708295:  // fields
                    return ((Form) bean).getFields();
            }
            return super.propertyGet(bean, propertyName, quiet);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
            switch (propertyName.hashCode()) {
                case 3355:  // id
                    ((Form) bean).setId((String) newValue);
                    return;
                case 3373707:  // name
                    ((Form) bean).setName((String) newValue);
                    return;
                case 937767745:  // sqlStatement
                    ((Form) bean).setSqlStatement((String) newValue);
                    return;
                case -1274708295:  // fields
                    ((Form) bean).setFields((List<FormField>) newValue);
                    return;
            }
            super.propertySet(bean, propertyName, newValue, quiet);
        }

    }

    ///CLOVER:ON
    //-------------------------- AUTOGENERATED END --------------------------
}
