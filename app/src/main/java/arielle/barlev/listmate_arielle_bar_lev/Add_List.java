package arielle.barlev.listmate_arielle_bar_lev;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class Add_List extends Fragment {

    private EditText list_name;
    private Button create;
    private Firebase_Helper helper;
    private Utilities utilities;
    private String Uid;

    private void init(View view) {
        list_name = view.findViewById(R.id.list_name);
        create = view.findViewById(R.id.create);

        Bundle args = getArguments();
        if (args != null) {
            Uid = args.getString("Uid");
        }

        helper = new Firebase_Helper(requireContext());
        utilities = new Utilities();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String list_name_content = list_name.getText().toString().trim();

                if (list_name_content.isEmpty()) {
                    utilities.make_snackbar(requireContext(), "List name cannot be empty.");
                    return;
                }

                helper.create_list(Uid, list_name_content);

                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                    utilities.make_snackbar(requireContext(), "List created!");
                } else {
                    utilities.make_snackbar(requireContext(), "List created! Please navigate back.");
                }
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This is called when the fragment is attached to its host activity.
        // You can get a reference to the activity here if needed for direct communication.
    }

    // Changed: Optional: Override onDetach to release context reference
    @Override
    public void onDetach() {
        super.onDetach();
        // This is called when the fragment is detached from its host activity.
    }
}