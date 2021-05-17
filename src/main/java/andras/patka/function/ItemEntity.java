package andras.patka.function;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class ItemEntity extends TableServiceEntity {
    String name;
    String category;

    public ItemEntity(String name, String category) {
        this.partitionKey = category;
        this.rowKey = name;
    }

    public ItemEntity() {}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    
}
