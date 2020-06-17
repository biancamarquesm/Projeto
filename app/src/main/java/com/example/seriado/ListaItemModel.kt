package com.example.seriado

class ListaItemModel {

    var listaId: Int = 0
    var listaTexto: String? = null
    var listaData: String? = null

    constructor()

    constructor(lista_id: Int, lista_texto: String, lista_data: String) {
        this.listaId = lista_id
        this.listaTexto = lista_texto
        this.listaData = listaData
    }

    companion object {
        val LISTA_TABLE_NAME = "lista_db"

        val LISTA_ID_COLUMN = "id"
        val LISTA_TEXT_COLUMN = "listaText"
        val LISTA_DATA_COLUMN = "listaData"

        val CREATE_TABLE = (
                "CREATE TABLE "
                + LISTA_TABLE_NAME  + "("
                + LISTA_ID_COLUMN   + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LISTA_TEXT_COLUMN + " TEXT, "
                + LISTA_DATA_COLUMN +  " DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")")

    }
}