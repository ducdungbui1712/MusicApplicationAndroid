package com.example.musicapplication.Fragment.Home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.musicapplication.Fragment.BannerSongsFragment;
import com.example.musicapplication.Fragment.Singer.SingerTabFragment;
import com.example.musicapplication.Model.Album;
import com.example.musicapplication.Model.Banner;
import com.example.musicapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeBannerFragment extends Fragment {
    ImageSlider imageSlider;
    FirebaseFirestore firebaseFirestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_banner, container, false);
        imageSlider = view.findViewById(R.id.imageSlider);
        firebaseFirestore = FirebaseFirestore.getInstance();
        ArrayList<SlideModel> imageList = new ArrayList<>();
        ArrayList<Banner> banners = new ArrayList<>();
        firebaseFirestore.collection("Banner")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Lấy giá trị của field image trong tài liệu hiện tại và add vào imageList
                            String id = document.getString("id");
                            String image = document.getString("image");
                            imageList.add(new SlideModel(image, ScaleTypes.FIT));
                            Banner banner = new Banner(id,image);
                            banners.add(banner);
                        }
                        // Hiển thị danh sách hình ảnh trong imageSlider
                        imageSlider.setImageList(imageList, ScaleTypes.FIT);

                        imageSlider.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onItemSelected(int i) {
                                Log.d("TAG", "pos :" + banners.get(i).getId());
                                Bundle bundle = new Bundle();
                                bundle.putString("idBanner", banners.get(i).getId());
                                BannerSongsFragment bannerSongsFragment = new BannerSongsFragment();
                                bannerSongsFragment.setArguments(bundle);

                                FragmentTransaction fragmentTransaction = ((AppCompatActivity)getContext())
                                        .getSupportFragmentManager()
                                        .beginTransaction();
                                fragmentTransaction.replace(R.id.fragmentLayout, bannerSongsFragment);
                                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }

                            @Override
                            public void doubleClick(int i) {
                                //do nothing
                            }
                        });

                    } else {
                        Log.d("TAG", "Error getting documents: ");
                    }
                });
        return view;
    }
}