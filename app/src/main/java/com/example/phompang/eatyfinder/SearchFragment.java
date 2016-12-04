package com.example.phompang.eatyfinder;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.phompang.eatyfinder.adapter.CategoryAdapter;
import com.example.phompang.eatyfinder.model.Party;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fi.foyt.foursquare.api.entities.Category;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements CategoryAdapter.ViewHolder.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mDatabaseReference;

    private Set<Category> categorySet;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        setHasOptionsMenu(false);
    }

    @BindView(R.id.searchAppName)
    TextView mSearchAppName;
    @BindView(R.id.searchBg)
    ImageView mSearchBg;
    @BindView(R.id.searchBar)
    EditText mSearchBar;
    @BindView(R.id.genreList)
    RecyclerView mCategoryList;

    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
//
//    private int[] photos = {R.drawable.breakfast, R.drawable.dessert, R.drawable.japanese, R.drawable.pizza};
//    private String[] titles = {"Breakfast", "Dessert", "Japanese", "Pizza"};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, v);

//        ArrayList<Genre> genres = new ArrayList<>();
//        for (int i=0;i<=3;i++) {
//            Genre g = new Genre();
//            g.setPhoto(photos[i]);
//            g.setTitle(titles[i]);
//            genres.add(g);
//        }

        mCategoryList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mCategoryList.setLayoutManager(layoutManager);
//        GenreAdapter adapter = new GenreAdapter(getActivity(), genres);
//        mCategoryList.setAdapter(adapter);

        mSearchAppName.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/monofur_powerline.ttf"));
        Glide.with(this).load(R.drawable.breakfast).centerCrop().into(mSearchBg);

        categorySet = new LinkedHashSet<>();
        categoryList = new ArrayList<>(categorySet);
        displayCategory();
        mDatabaseReference.child("parties").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Category category = snapshot.getValue(Party.class).getCategory();
                    categorySet.add(category);
                }
                categoryList = new ArrayList<>(categorySet);
                displayCategory();

//                Log.d("category", categorySet.toString());
//                for (Category category: categorySet) {
//                    Gson gson = new Gson();
//                    Icon icon = gson.fromJson(category.getIcon(), Icon.class);
//                    Log.d("icon", icon.getPrefix());
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }

    @OnClick(R.id.searchBar)
    public void onSearchClick() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, SearchDetailFragment.newInstance(null)).addToBackStack(null).commit();
    }

    private void displayCategory() {
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this);
        mCategoryList.setAdapter(categoryAdapter);
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

    @Override
    public void onClick(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, SearchDetailFragment.newInstance(categoryList.get(position).getName())).addToBackStack(null).commit();
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
}
