package com.example.phompang.eatyfinder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.Interface.CategorySearchStrategy;
import com.example.phompang.eatyfinder.Interface.SearchStrategy;
import com.example.phompang.eatyfinder.Interface.TitleSearchStrategy;
import com.example.phompang.eatyfinder.model.Party;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchDetailFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    private SearchStrategy strategy;

    public SearchDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SearchDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchDetailFragment newInstance(String param1) {
        SearchDetailFragment fragment = new SearchDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            strategy = new CategorySearchStrategy();
        } else {
            strategy = new TitleSearchStrategy();
        }
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @BindView(R.id.searchBar)
    EditText mSearchbar;
    @BindView(R.id.list)
    RecyclerView mList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search_detail, container, false);
        ButterKnife.bind(this, v);

        mSearchbar.setText(mParam1);
        mSearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                strategy = new TitleSearchStrategy();
                mParam1 = editable.toString();
                postsQuery = getQuery(mDatabaseReference);
                mAdapter = new FirebaseRecyclerAdapter<Party, AllFragment.PartyCardViewHolder>(Party.class, R.layout.party_card_layout, AllFragment.PartyCardViewHolder.class, postsQuery) {
                    @Override
                    protected void populateViewHolder(final AllFragment.PartyCardViewHolder viewHolder, final Party model, final int position) {
                        mStorageReference.child("photos/" + model.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Context ctx = getContext();
                                if (ctx != null) {
                                    Glide.with(ctx).load(uri).centerCrop().into(viewHolder.mImg);
                                }
                            }
                        });
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
                    }
                };
                mList.setAdapter(mAdapter);
            }
        });

        return v;
    }


    private FirebaseRecyclerAdapter<Party, AllFragment.PartyCardViewHolder> mAdapter;
    private Query postsQuery;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        postsQuery = getQuery(mDatabaseReference);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        mList.setLayoutManager(layoutManager);

        mAdapter = new FirebaseRecyclerAdapter<Party, AllFragment.PartyCardViewHolder>(Party.class, R.layout.party_card_layout, AllFragment.PartyCardViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final AllFragment.PartyCardViewHolder viewHolder, final Party model, final int position) {
                mStorageReference.child("photos/" + model.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Context ctx = getContext();
                        if (ctx != null) {
                            Glide.with(ctx).load(uri).centerCrop().into(viewHolder.mImg);
                        }
                    }
                });
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
            }
        };
        mList.setAdapter(mAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return strategy.search(databaseReference, mParam1);
    }
}
