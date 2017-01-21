package com.booknara.voicerecognition; //sursa din care este descarcata baza de date a programului
// in import se introduc toate librariile de care programul are nevoie pentru a-si lua informatiile necesare rularii
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.booknara.util.VoiceRecognitionIntentFactory;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int REQUEST_CODE = 1;
	private ListView mResultList;
	private Button mMicButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mMicButton = (Button) findViewById(R.id.mic_button);
		mResultList = (ListView) findViewById(R.id.result_list);

		PackageManager pm = getPackageManager(); //feră metode pentru interogarea și maniuplating pachetelor instalate și permisiunile aferente
		List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		if (activities == null || activities.size() == 0) {
		   mMicButton.setEnabled(false);
		   Toast.makeText(getApplicationContext(), "Nu este suportat", Toast.LENGTH_LONG).show(); // toast = mesaj de asistenta in caz de eroare
		}
		mMicButton.setOnClickListener(new OnClickListener() {
		   @Override
		   public void onClick(View v) {
			   runVoiceRecognition();
		   }
		});
	}
//Casuta care apare pe ecran dupa apasarea butonului de microfon pentru a putea rosti cuvantull
	private void runVoiceRecognition() {
		Intent intent = VoiceRecognitionIntentFactory.getFreeFormRecognizeIntent("Rostiți numele dorit");
		startActivityForResult(intent, REQUEST_CODE);
	}

    // Urmatoarea secventa se apeleaza dupa ce se rosteste numele contactului dorit
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		List<String> contacts = getContactList(); // lista de contacte
		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // afiseaza pe display ce gaseste
            // face intersectia datelor // corelatie intre ce se rosteste si ce gaseste
            matches.retainAll(contacts);
			mResultList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, matches)); //pune in lista elementele din matches
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}


    // returneaza lista de contacte
	public List<String> getContactList() {

        List<String> names = new LinkedList<>(); // initializeaza o lista goale
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null); // cauta in Contacte numele si nr de telefon
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)); // organizeaza in baza de date si seleccteaza coloana de care avem nevoie
            names.add(name.toLowerCase()); //adauga numele gasite in lista si transforma toate literele mari in litere mici
        }
        phones.close();

        return names; //returneaza numele
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.voice, menu);
		return true;
	}

}
