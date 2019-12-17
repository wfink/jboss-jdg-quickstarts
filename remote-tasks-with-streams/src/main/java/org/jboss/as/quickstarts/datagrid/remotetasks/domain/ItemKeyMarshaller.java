package org.jboss.as.quickstarts.datagrid.remotetasks.domain;

import java.io.IOException;
import java.io.Serializable;

import org.infinispan.protostream.MessageMarshaller;


public class ItemKeyMarshaller implements MessageMarshaller<ItemKey>, Serializable {
   /**
    *
    */
   private static final long serialVersionUID = -85141079633454161L;

   @Override
   public ItemKey readFrom(ProtoStreamReader reader) throws IOException {
      Long id = reader.readLong("id");
      String company = reader.readString("company");
      
      

      //Map roleAttribute = reader.readObject("roleAttribute", HashMap.class);
      ItemKey itemKey = new ItemKey(id, company);
      //Person person = new Person(id, name, age);

      return itemKey;
   }

   @Override
   public void writeTo(ProtoStreamWriter writer, ItemKey itemKey) throws IOException {
      writer.writeLong("id", itemKey.getId());
      writer.writeString("company", itemKey.getCompany());

      //writer.writeEnum("waiver", person.getWaiver());

    //  writer.writeObject("roleAttribute", person.getRoleAttribute(), HashMap.class);

     }

   @Override
   public Class<? extends ItemKey> getJavaClass() {
      return ItemKey.class;
   }

   @Override
   public String getTypeName() {
      return "quickstart.ItemKey";
   }
}
