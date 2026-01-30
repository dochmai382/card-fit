package org.example.cardfit.infrastructure.excel;

public record ColumnMapping(
        int dateIndex,
        int storeNameIndex,
        int amountIndex,
        int typeIndex
) {
}
