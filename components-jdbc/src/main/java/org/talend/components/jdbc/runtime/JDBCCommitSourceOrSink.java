// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.jdbc.runtime;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.avro.Schema;
import org.talend.components.api.component.runtime.SourceOrSink;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.jdbc.ComponentConstants;
import org.talend.components.jdbc.RuntimeSettingProvider;
import org.talend.components.jdbc.runtime.setting.AllSetting;
import org.talend.daikon.NamedThing;
import org.talend.daikon.properties.ValidationResult;

public class JDBCCommitSourceOrSink implements SourceOrSink {

    private static final long serialVersionUID = -7226558840084293603L;

    public ComponentProperties properties;

    @Override
    public ValidationResult initialize(RuntimeContainer runtime, ComponentProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    private static ValidationResult fillValidationResult(ValidationResult vr, Exception ex) {
        if (vr == null) {
            return null;
        }
        vr.setMessage(ex.getMessage());
        vr.setStatus(ValidationResult.Result.ERROR);
        return vr;
    }

    @Override
    public ValidationResult validate(RuntimeContainer runtime) {
        ValidationResult vr = new ValidationResult();
        try {
            doCommitAction(runtime);
        } catch (Exception ex) {
            fillValidationResult(vr, ex);
        }
        return vr;
    }

    @Override
    public List<NamedThing> getSchemaNames(RuntimeContainer runtime) throws IOException {
        return null;
    }

    @Override
    public Schema getEndpointSchema(RuntimeContainer runtime, String tableName) throws IOException {
        return null;
    }

    public void doCommitAction(RuntimeContainer runtime) throws SQLException {
        String refComponentId = ((RuntimeSettingProvider) properties).getRuntimeSetting().getReferencedComponentId();
        if (refComponentId != null && runtime != null) {
            java.sql.Connection conn = (java.sql.Connection) runtime.getComponentData(refComponentId,
                    ComponentConstants.CONNECTION_KEY);
            if (conn != null && !conn.isClosed()) {
                conn.commit();

                AllSetting setting = ((RuntimeSettingProvider) properties).getRuntimeSetting();
                if (setting.getCloseConnection()) {
                    conn.close();
                }
            }
        } else {
            throw new RuntimeException("Can't find the connection by the key");
        }
    }

}
