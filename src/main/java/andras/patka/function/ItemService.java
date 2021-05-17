package andras.patka.function;


import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.table.*;
import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ItemService {

    private String storageConnectionString;

    public ItemService(String storageConnectionString) {
        this.storageConnectionString = storageConnectionString;
    }

    public void saveToTable(String name, Categories category) {
        try {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount =
                CloudStorageAccount.parse(storageConnectionString);

            // Create the table client.
            CloudTableClient tableClient = storageAccount.createCloudTableClient();

            String tableName = "Items";

            // Create a cloud table object for the table.
            CloudTable cloudTable = tableClient.getTableReference(tableName);
            cloudTable.createIfNotExists();

            // Create a new customer entity.
            ItemEntity itemEntity = new ItemEntity(name, category.toString());

            // Create an operation to add the new string to the strings table.
            TableOperation insertString = TableOperation.insertOrReplace(itemEntity);

            // Submit the operation to the table service.
            cloudTable.execute(insertString);
        }
        catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
    }

    public List<String> getItemsOfCategory(Categories category) {
        try {
            // Define constants for filters.
            final String PARTITION_KEY = "PartitionKey";
        
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        
            // Create the table client.
            CloudTableClient tableClient = storageAccount.createCloudTableClient();
        
            // Create a cloud table object for the table.
            CloudTable cloudTable = tableClient.getTableReference("Items");
        
            // Create a filter condition where the partition key is a category.
            String partitionFilter = TableQuery.generateFilterCondition(PARTITION_KEY, QueryComparisons.EQUAL, category.toString());
        
            // Specify a partition query, using "Smith" as the partition key filter.
            TableQuery<ItemEntity> partitionQuery = TableQuery.from(ItemEntity.class).where(partitionFilter);

            return StreamSupport.stream(cloudTable.execute(partitionQuery).spliterator(), false)
                .map(ItemEntity::getName)
                .collect(Collectors.toList());
        }
        catch (Exception e) {
            // Output the stack trace.
            e.printStackTrace();
        }
        return null;
    }
    
}
