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
package org.talend.components.salesforce.tsalesforcegetdeleted;

import java.util.EnumSet;
import java.util.Set;

import org.talend.components.api.component.ConnectorTopology;
import org.talend.components.api.properties.ComponentProperties;
import org.talend.components.salesforce.SalesforceDefinition;
import org.talend.components.salesforce.runtime.SalesforceSource;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.runtime.RuntimeInfo;

public class TSalesforceGetDeletedDefinition extends SalesforceDefinition {

    public static final String COMPONENT_NAME = "tSalesforceGetDeleted"; //$NON-NLS-1$

    public TSalesforceGetDeletedDefinition() {
        super(COMPONENT_NAME);
    }

    @Override
    public String getPartitioning() {
        return AUTO;
    }

    @Override
    public Class<? extends ComponentProperties> getPropertyClass() {
        return TSalesforceGetDeletedProperties.class;
    }

    @Override
    public RuntimeInfo getRuntimeInfo(ComponentProperties properties, ConnectorTopology componentType) {
        if (componentType == ConnectorTopology.OUTGOING) {
            return getCommonRuntimeInfo(this.getClass().getClassLoader(), SalesforceSource.class);
        } else {
            return null;
        }
    }

    @Override
    public Set<ConnectorTopology> getSupportedConnectorTopologies() {
        return EnumSet.of(ConnectorTopology.OUTGOING);
    }

}
