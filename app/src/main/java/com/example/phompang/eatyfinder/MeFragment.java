package com.example.phompang.eatyfinder;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.app.DpiUtils;
import com.example.phompang.eatyfinder.model.Party;
import com.example.phompang.eatyfinder.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnLogoutListener mListener;

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.meEmail)
    TextView mEmail;
    @BindView(R.id.toolbarImg)
    ImageView meImg;
    @BindView(R.id.list)
    RecyclerView mList;

    private FirebaseRecyclerAdapter mAdapter;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;

//    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, v);
//
//        collapsingToolbarLayout = (CollapsingToolbarLayout) v.findViewById(R.id.collapsingToolbarLayout);
//
//        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
//            collapsingToolbarLayout.setTitle(user.getDisplayName());
            Glide.with(this).load(user.getPhotoUrl()).centerCrop().into(meImg);
            mName.setText(user.getDisplayName());
            mEmail.setText(user.getEmail());
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();

        Query postsQuery = getQuery(mDatabaseReference);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        mList.setLayoutManager(layoutManager);

        mAdapter = new FirebaseRecyclerAdapter<Party, AllFragment.PartyCardViewHolder>(Party.class, R.layout.party_card_layout, AllFragment.PartyCardViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final AllFragment.PartyCardViewHolder viewHolder, final Party model, final int position) {
                if (getContext() != null) {
                    Glide.with(getContext()).using(new FirebaseImageLoader()).load(mStorageReference.child("photos/" + model.getPhoto())).centerCrop().into(viewHolder.mImg);
                }
                viewHolder.mTitle.setText(model.getTitle());
                viewHolder.mPrice.setText(String.format("฿ %s", Double.toString(model.getPricePerPerson())));
                viewHolder.mTime.setText(model.getDate() + " " + model.getTime());
                viewHolder.mPeople.setText("(" + model.getCurrentPeople() + "/" + model.getRequiredPeople() + " คน)");
                viewHolder.mDesc.setText(model.getDesc());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getActivity(), PartyDetailActivity.class);
                        i.putExtra("party", model);
                        i.putExtra("key", mAdapter.getRef(position).getKey());
                        startActivity(i);
                    }
                });
                if (null != viewHolder.mAttendee && viewHolder.mAttendee.getChildCount() > 0) {
                    viewHolder.mAttendee.removeAllViews();
                }

                int width = DpiUtils.toPixels(30, getResources().getDisplayMetrics());
                int height = DpiUtils.toPixels(30, getResources().getDisplayMetrics());

                for (final User u : model.getAttendees().values()) {
                    CircularImageView cv = new CircularImageView(getContext());
                    cv.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                    Glide.with(getContext()).load(u.getPhoto()).into(cv);

                    TextView textView = new TextView(getContext());
                    textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (u.getPeople() > 1) {
                        textView.setText(String.format(Locale.getDefault(), "+%d", u.getPeople() - 1));
                    } else {
                        textView.setText("");
                    }
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                    viewHolder.mAttendee.addView(cv);
                    viewHolder.mAttendee.addView(textView);
                }
            }
        };
        mList.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.meLogout)
    public void logout() {
        mListener.onLogout();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLogoutListener) {
            mListener = (OnLogoutListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLogoutListener {
        void onLogout();
    }

    public Query getQuery(DatabaseReference databaseReference) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return databaseReference.child("parties").orderByChild("owner").equalTo(uid).limitToLast(20);
    }

}
