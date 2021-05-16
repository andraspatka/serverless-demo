package andras.patka.function;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class StringEntity extends TableServiceEntity {
    String name;

    public StringEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
