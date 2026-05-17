package pe.pixelstudio.pixelerp.data.model

object PlantillasCampos {
    val predefinidos = listOf(
        CampoProducto(0, 0, "Código interno", TipoDato.ALFANUMERICO, false, true, 0, true),
        CampoProducto(0, 0, "Código de barras", TipoDato.ALFANUMERICO, false, true, 0, true),
        CampoProducto(0, 0, "Descripción", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Categoría", TipoDato.LISTA, false, true, 0, true),
        CampoProducto(0, 0, "Marca", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Modelo", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Unidad de medida", TipoDato.LISTA, true, true, 0, true, opcionesLista = listOf("Unidad", "Kg", "gr", "Litro", "Metro")),
        CampoProducto(0, 0, "Precio de compra", TipoDato.MONEDA, false, true, 0, true, moneda = Moneda.PEN, prefijo = "S/ "),
        CampoProducto(0, 0, "Precio de venta", TipoDato.MONEDA, true, true, 0, true, moneda = Moneda.PEN, prefijo = "S/ "),
        CampoProducto(0, 0, "Stock actual", TipoDato.DECIMAL, false, true, 0, true, cantidadDecimales = 2),
        CampoProducto(0, 0, "Stock mínimo", TipoDato.DECIMAL, false, true, 0, true, cantidadDecimales = 2),
        CampoProducto(0, 0, "Lote", TipoDato.ALFANUMERICO, false, true, 0, true),
        CampoProducto(0, 0, "Fecha de vencimiento", TipoDato.FECHA, false, true, 0, true),
        CampoProducto(0, 0, "Fecha de fabricación", TipoDato.FECHA, false, true, 0, true),
        CampoProducto(0, 0, "Número de serie", TipoDato.ALFANUMERICO, false, true, 0, true),
        CampoProducto(0, 0, "Proveedor", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Ubicación en almacén", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Peso", TipoDato.DECIMAL, false, true, 0, true, cantidadDecimales = 3, sufijo = " Kg"),
        CampoProducto(0, 0, "Color", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Talla", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Material", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Registro sanitario", TipoDato.ALFANUMERICO, false, true, 0, true),
        CampoProducto(0, 0, "Presentación", TipoDato.TEXTO, false, true, 0, true),
        CampoProducto(0, 0, "Observaciones", TipoDato.TEXTO, false, true, 0, true)
    )
}
