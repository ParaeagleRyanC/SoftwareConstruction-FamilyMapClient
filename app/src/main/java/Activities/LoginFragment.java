package Activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.familymapclient.R;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Request.LoginRequest;
import Request.RegisterRequest;
import Tasks.LoginTask;
import Tasks.RegisterTask;

public class LoginFragment extends Fragment {

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    private EditText serverHost;
    private EditText serverPort;
    private EditText username;
    private EditText password;
    private EditText firstName;
    private EditText lastName;
    private EditText email;

    private String serverHostString;
    private String serverPortString;
    private String usernameString;
    private String passwordString;
    private String firstNameString;
    private String lastNameString;
    private String emailString;

    private Button maleButton;
    private Button femaleButton;
    private Button signInButton;
    private Button registerButton;

    private Listener listener;
    public interface Listener { void notifyDone(); }
    public void registerListener(Listener listener) { this.listener = listener; }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // initialize login and register requests
        loginRequest = new LoginRequest(null,null);
        registerRequest = new RegisterRequest(null, null, null,
                                        null, null, null);

        // assign local EditTexts
        serverHost = view.findViewById(R.id.serverHostNumber);
        serverPort = view.findViewById(R.id.serverPortNumber);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.emailAddress);

        // set listeners
        serverHost.addTextChangedListener(textWatcher);
        serverPort.addTextChangedListener(textWatcher);
        username.addTextChangedListener(textWatcher);
        password.addTextChangedListener(textWatcher);
        firstName.addTextChangedListener(textWatcher);
        lastName.addTextChangedListener(textWatcher);
        email.addTextChangedListener(textWatcher);

        // assign local Buttons
        maleButton = view.findViewById(R.id.radioMale);
        femaleButton = view.findViewById(R.id.radioFemale);
        signInButton = view.findViewById(R.id.signInButton);
        registerButton = view.findViewById(R.id.registerButton);
        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerRequest.setGender("m");
                enableButton();
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerRequest.setGender("f");
                enableButton();
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginRequest.setUsername(usernameString);
                loginRequest.setPassword(passwordString);

                try {
                    // call login task method
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            String welcomeMsg = bundle.getString("message_key");
                            Toast.makeText(view.getContext(), welcomeMsg,Toast.LENGTH_SHORT).show();
                            if (welcomeMsg.contains("Hi")) {
                                if (listener != null) {
                                    listener.notifyDone();
                                }
                            }
                        }
                    };

                    LoginTask task = new LoginTask(handler, loginRequest, serverHostString, serverPortString);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);
                } catch (Exception e) {
                    Log.e("LoginFragment",e.getMessage(),e);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerRequest.setUsername(usernameString);
                registerRequest.setPassword(passwordString);
                registerRequest.setFirstName(firstNameString);
                registerRequest.setLastName(lastNameString);
                registerRequest.setEmail(emailString);

                try {
                    // call register task method
                    Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            String welcomeMsg = bundle.getString("message_key");
                            Toast.makeText(view.getContext(), welcomeMsg,Toast.LENGTH_SHORT).show();
                            if (welcomeMsg.contains("Hi")) {
                                if (listener != null) {
                                    listener.notifyDone();
                                }
                            }
                        }
                    };

                    RegisterTask task = new RegisterTask(handler, registerRequest, serverHostString, serverPortString);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(task);
                } catch (Exception e) {
                    Log.e("LoginFragment",e.getMessage(),e);
                }
            }
        });
        return view;
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void afterTextChanged(Editable editable) {
            enableButton();
        }
    };

    private void enableButton() {
        serverHostString = serverHost.getText().toString();
        serverPortString = serverPort.getText().toString();
        usernameString = username.getText().toString();
        passwordString = password.getText().toString();
        firstNameString = firstName.getText().toString();
        lastNameString = lastName.getText().toString();
        emailString = email.getText().toString();

        signInButton.setEnabled(false);
        registerButton.setEnabled(false);

        if (!serverHostString.isEmpty() && !serverPortString.isEmpty() &&
            !usernameString.isEmpty() && !passwordString.isEmpty()) {
            if (!firstNameString.isEmpty() && !lastNameString.isEmpty() &&
                !emailString.isEmpty() && registerRequest.getGender() != null) {
                signInButton.setEnabled(true);
                registerButton.setEnabled(true);
            }
            else {
                signInButton.setEnabled(true);
            }
        }
    }
}