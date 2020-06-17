package com.example.seriado

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuAdapter
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private var coordinatorLayout: CoordinatorLayout? = null
    private var recyclerView: RecyclerView? = null
    private var ItemsList = ArrayList<ListaItemModel>()
    private var MAdapter: ListaItemAdapter? = null

    private var db: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)

        controle()
    }

    private fun controle() {
        coordinatorLayout = findViewById(R.id.layout_main)
        recyclerView = findViewById(R.id.recycler_main)
        db = DBHelper(this)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { showDialog(false, null, -1) }


        // exibe resultados//

        ItemsList.addAll(db!!.ItensList)
        MAdapter = ListaItemAdapter(this, ItemsList)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = MAdapter

        //funcao de click//

        recyclerView!!.addOnItemTouchListener(ItemLongPressListener(this, recyclerView!!,
            object : ItemLongPressListener.ClickListener{

                override fun onClick(view: View, position: Int) {
                }

                override fun onLongClick(view: View?, position: Int) {
                   showActionDialog(position)
                }

            }))

    }

    private fun showActionDialog(position: Int) {
        val options = arrayOf<CharSequence>(getString(R.string.editar), getString(R.string.excluir), getString(R.string.excluirTudo))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.tituloOpcao))
        builder.setItems(options){ dialog, itemIndex ->
            when(itemIndex) {
                0 -> showDialog(true, ItemsList[position], position)
                1 -> deleteListaItem(position)
                2 -> deleteTodosItens()
                else -> Toast.makeText(applicationContext, getString(R.string.toastErro), Toast.LENGTH_SHORT).show()
            }
        }

        builder.show()
    }
     //deleta um registro de cada//
    private fun deleteListaItem(position: Int) {
        db!!.deleteListaItem(ItemsList[position])

        ItemsList.removeAt(position)
        MAdapter!!.notifyItemRemoved(position)
    }

    //deleta tudo//
    private fun deleteTodosItens() {
        db!!.deleteTodosListaItem()

        ItemsList.removeAll(ItemsList)
        MAdapter!!.notifyDataSetChanged()
    }


    private fun showDialog(isUpdate: Boolean, listaItemModel: ListaItemModel?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(applicationContext)
        val view = layoutInflaterAndroid.inflate(R.layout.lista_dialog, null)

        val userInput = AlertDialog.Builder(this@MainActivity)
        userInput.setView(view)

        val input = view.findViewById<EditText>(R.id.dialogText)
        val titulo = view.findViewById<TextView>(R.id.dialogTitle)
        titulo.text = if (!isUpdate) getString(R.string.novo) else getString(R.string.editar)

        if(isUpdate && listaItemModel != null){
            input.setText(listaItemModel!!.listaTexto)
        }

        userInput
            .setCancelable(false)
            .setPositiveButton(if (isUpdate) getString(R.string.atualizar) else getString(R.string.salvar)) { dialogBox, id -> }
            .setNegativeButton(getString(R.string.cancelar)) { dialogBox, id -> dialogBox.cancel() }

        val alertDialog = userInput.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(input.text.toString())) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.toastSeriado),
                    Toast.LENGTH_SHORT
                ).show()
                return@OnClickListener
            } else {
                alertDialog.dismiss()
            }

            if(isUpdate && listaItemModel != null){
                updateListaItem(input.text.toString(), position)
            }else {
                createListaItem(input.text.toString())
            }
        })
    }

    private fun updateListaItem(listaTexo: String, position: Int) {
       val item = ItemsList[position]
        item.listaTexto = (listaTexo)
        db!!.updateListaItem(item)

        ItemsList[position] = item
        MAdapter!!.notifyItemChanged(position)
    }

    private fun createListaItem(listaText: String) {
       val item=  db!!.insertListaItem(listaText)
       val novoItem = db!!.getListaItem(item)

        if(novoItem !=null){
            ItemsList.add(0,novoItem)
            MAdapter!!.notifyDataSetChanged()
        }
    }

}


