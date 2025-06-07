package arielle.barlev.listmate_arielle_bar_lev;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class Add_Item extends Fragment {

    private EditText item;

    private Button create;

    private String Uid;
    private String list_id;

    private Firebase_Helper helper;
    private Utilities utilities;

    private void init(View view) {
        item = view.findViewById(R.id.item);
        create = view.findViewById(R.id.create);

        Bundle args = getArguments();
        if (args != null) {
            Uid = args.getString("Uid");
            list_id = args.getString("list_id");
        }

        helper = new Firebase_Helper(requireContext());
        utilities = new Utilities();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add__item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item_content = item.getText().toString().trim();

                if (item_content.isEmpty()) {
                    utilities.make_snackbar(requireContext(), "List name cannot be empty.");
                    return;
                }

                helper.add_item(list_id, item_content, false);

                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                    utilities.make_snackbar(requireContext(), "Item added!");
                } else {
                    utilities.make_snackbar(requireContext(), "Item! Please navigate back.");
                }
            }
        });
    }
}