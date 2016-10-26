package com.example.phompang.eatyfinder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllFragment.OnAddClickedListener} interface
 * to handle interaction events.
 * Use the {@link AllFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnAddClickedListener mListener;

    private FirebaseRecyclerAdapter<Party, PartyCardViewHolder> mAdapter;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;

    public AllFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AllFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AllFragment newInstance(String param1, String param2) {
        AllFragment fragment = new AllFragment();
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

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
    }

    @BindView(R.id.allList)
    RecyclerView mAll;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query postsQuery = getQuery(mDatabaseReference);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mAll.setLayoutManager(layoutManager);

        mAdapter = new FirebaseRecyclerAdapter<Party, PartyCardViewHolder>(Party.class, R.layout.party_card_layout, PartyCardViewHolder.class, postsQuery) {
            @Override
            protected void populateViewHolder(final PartyCardViewHolder viewHolder, Party model, int position) {
                mStorageReference.child("photos/" + model.getPhoto()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Context ctx = getContext();
                        if (ctx != null) {
                            Glide.with(ctx).load(uri).into(viewHolder.mImg);
                        }
                    }
                });
                viewHolder.mTitle.setText(model.getTitle());
                viewHolder.mPrice.setText("฿ " + Double.toString(model.getPricePerPerson()));
                viewHolder.mTime.setText(model.getDate() + " " + model.getTime());
                viewHolder.mPeople.setText("(" + model.getCurrentPeople() + "/" + model.getRequiredPeople() + " คน)");
                viewHolder.mDesc.setText(model.getDesc());
            }
        };
        mAll.setAdapter(mAdapter);

    }

    @OnClick(R.id.allAdd)
    public void add() {
        Toast.makeText(getContext(), "test", Toast.LENGTH_SHORT).show();
        onClicked();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onClicked() {
        if (mListener != null) {
            mListener.onClicked();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddClickedListener) {
            mListener = (OnAddClickedListener) context;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
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
    public interface OnAddClickedListener {
        // TODO: Update argument type and name
        void onClicked();
    }

    static class PartyCardViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.partyCardTitle)
        TextView mTitle;
        @BindView(R.id.partyCardPrice)
        TextView mPrice;
        @BindView(R.id.partyCardImg)
        ImageView mImg;
        @BindView(R.id.partyCardTime)
        TextView mTime;
        @BindView(R.id.partyCardPeople)
        TextView mPeople;
        @BindView(R.id.partyCardDesc)
        TextView mDesc;
        public PartyCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("parties");
    }
}
