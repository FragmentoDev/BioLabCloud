package com.example.fragmento.biolabcloud.Tabs;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.fragmento.biolabcloud.R;
import com.example.fragmento.biolabcloud.formularios;
import com.example.fragmento.biolabcloud.modelo.Organismo;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Tab3 extends Fragment implements Serializable {
    ArrayList<Organismo> listPerson = new ArrayList<Organismo>();
    ArrayAdapter<Organismo> arrayAdapterPersona;
    ListView listV_personas;
    android.support.v7.widget.SearchView sv;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Organismo personaSelected;

    private OnFragmentInteractionListener mListener;

    public Tab3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.icon_add).setVisible(true);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab3, container, false);

        setHasOptionsMenu(true);
        android.support.v7.widget.Toolbar actionToolbar = (android.support.v7.widget.Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(actionToolbar);
        getActivity().setTitle("Lista de Organismos");
        actionToolbar.setTitleTextColor(Color.WHITE);
        sv = v.findViewById(R.id.et_buscar);

        listV_personas = v.findViewById(R.id.lv_datosPersonas);

        inicializarFirebase();
        listarDatos();


        listPerson = new ArrayList<>();

        listV_personas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(getContext(), formularios.class);
                personaSelected = (Organismo) parent.getItemAtPosition(position);

                i.putExtra("objeto", (personaSelected));
                i.putExtra("dedondevengo", 3);
                startActivity(i);
            }
        });


        registerForContextMenu(listV_personas);
        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId()==R.id.lv_datosPersonas){


            AdapterView.AdapterContextMenuInfo infoOrganismo = (AdapterView.AdapterContextMenuInfo) menuInfo;
            personaSelected = (Organismo) listV_personas.getItemAtPosition(infoOrganismo.position);

            String[] elementosMenu = getResources().getStringArray(R.array.elementosMenu);

            menu.setHeaderTitle(personaSelected.getNombre());
            for (int i=0; i<elementosMenu.length; i++ ){
                menu.add(Menu.NONE, i, i, elementosMenu[i]);
            }
        }
    }


    private void listarDatos() {
        databaseReference.child("Organismo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Organismo p = objSnaptshot.getValue(Organismo.class);
                    listPerson.add(p);

                    arrayAdapterPersona = new ArrayAdapter<Organismo>(getContext(), R.layout.mytextview, listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                    sv.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String text) {
                            return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String text) {

                            arrayAdapterPersona.getFilter().filter(text);

                            return false;
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo infoContacto = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        personaSelected = (Organismo) listV_personas.getItemAtPosition(infoContacto.position);

        switch (item.getItemId()) {
            case 0:
                Intent i = new Intent(getContext(), formularios.class);
                i.putExtra("objeto", personaSelected);
                i.putExtra("dedondevengo", 2);
                startActivity(i);
                break;
            case 1:
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Eliminar");
                dialogo1.setMessage("¿Esta seguro que quieres borrar este organismo?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Organismo objeto =  personaSelected;
                        String UID = objeto.getUid();
                        databaseReference.child("Organismo").child(UID).removeValue();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Toast.makeText(getContext(), "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogo1.show();

                break;
        }
        return true;
    }


    private void inicializarFirebase() {
        FirebaseApp.initializeApp(getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.icon_add: {
                Intent i = new Intent(getContext(), formularios.class);
                i.putExtra("dedondevengo", 1);
                startActivity(i);
            }
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}