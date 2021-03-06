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
package org.talend.components.kafka.runtime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.talend.components.api.component.runtime.RuntimableRuntime;
import org.talend.components.api.container.RuntimeContainer;
import org.talend.components.kafka.dataset.KafkaDatasetProperties;
import org.talend.components.kafka.datastore.KafkaDatastoreProperties;
import org.talend.components.kafka.output.KafkaOutputProperties;
import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.ValidationResult;

public class KafkaOutputPTransformRuntime extends PTransform<PCollection<IndexedRecord>, PDone>
        implements RuntimableRuntime<KafkaOutputProperties> {

    private static Logger LOG = LoggerFactory.getLogger(KafkaOutputPTransformRuntime.class);

    private transient KafkaOutputProperties properties;

    @Override
    public PDone apply(PCollection<IndexedRecord> objectPCollection) {
        // // FIXME the input should be IndexedRecord always, no? as there is no way to get properly avroRegistry here
        // yet
        // PCollection<IndexedRecord> test = objectPCollection.apply("ExtractIndexedRecord",
        // ParDo.of(new DoFn<Object, IndexedRecord>() {
        //
        // transient IndexedRecordConverter converter;
        //
        // transient AvroRegistry avroRegistry;
        //
        // @DoFn.ProcessElement
        // public void processElement(ProcessContext c) throws Exception {
        // if (c.element() == null)
        // return;
        // if (converter == null) {
        // avroRegistry = new AvroRegistry();
        // converter = avroRegistry.createIndexedRecordConverter(c.element().getClass());
        // }
        // c.output((IndexedRecord) converter.convertToAvro(c.element()));
        // }
        // }));

        // convert indexedRecord to KafkaIO.write want, KV<byte[], byte[]> is ok
        PCollection<KV<byte[], byte[]>> kafkaCollection = objectPCollection.apply("ExtractIndexedRecord",
                ParDo.of(new DoFn<IndexedRecord, KV<byte[], byte[]>>() {

                    @DoFn.ProcessElement
                    public void processElement(ProcessContext c) throws Exception {
                        // FIXME auto convert type before here, or use converter with some built-in auto convert
                        // function for basic type
                        Schema schema = c.element().getSchema();
                        LOG.debug("schema is \n" + schema.toString(true));
                        c.output(KV.of((byte[]) c.element().get(schema.getField("key").pos()),
                                (byte[]) c.element().get(schema.getField("value").pos())));
                    }
                }));

        return kafkaCollection.apply(KafkaIO.write()
                .withBootstrapServers(properties.getDatasetProperties().getDatastoreProperties().brokers.getValue())
                .withTopic(properties.getDatasetProperties().topic.getValue()).withKeyCoder(ByteArrayCoder.of())
                .withValueCoder(ByteArrayCoder.of()).updateProducerProperties(KafkaConnection.createOutputMaps(properties)));

    }

    @Override
    public ValidationResult initialize(RuntimeContainer container, KafkaOutputProperties properties) {
        this.properties = properties;
        return ValidationResult.OK;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(properties.toSerialized());
        out.writeUTF(properties.getDatasetProperties().toSerialized());
        out.writeUTF(properties.getDatasetProperties().getDatastoreProperties().toSerialized());
    }

    private void readObject(ObjectInputStream in) throws IOException {
        properties = Properties.Helper.fromSerializedPersistent(in.readUTF(), KafkaOutputProperties.class).object;
        KafkaDatasetProperties dataset = Properties.Helper.fromSerializedPersistent(in.readUTF(),
                KafkaDatasetProperties.class).object;
        KafkaDatastoreProperties datastore = Properties.Helper.fromSerializedPersistent(in.readUTF(),
                KafkaDatastoreProperties.class).object;
        properties.setDatasetProperties(dataset);
        properties.getDatasetProperties().setDatastoreProperties(datastore);
    }

}
