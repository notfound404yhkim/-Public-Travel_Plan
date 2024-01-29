package com.example.travelapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, PlaceSelectionListener {

    private static final String TAG = "MapFragment";
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private PlacesClient placesClient;
    private LatLng selectedPlace;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google Places API 초기화
        Places.initialize(requireContext(), getString(R.string.my_map_api_key));
        placesClient = Places.createClient(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // AutocompleteSupportFragment 설정
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setHint("검색...");
        autocompleteFragment.setOnPlaceSelectedListener(this);

        return view;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    selectedPlace = latLng; // 초기 위치 설정
                    mMap.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLastLocation();

        // 마커 클릭 이벤트 설정
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 클릭한 마커가 플레이스 마커인지 확인
                if (marker.getTag() != null && marker.getTag() instanceof String) {
                    // 플레이스 마커를 클릭한 경우
                    String placeId = (String) marker.getTag(); // 마커에 저장된 플레이스 ID 가져오기

                    // 플레이스 세부 정보 요청 생성
                    FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS));

                    // 플레이스 세부 정보 요청 실행
                    placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                        Place place = response.getPlace();

                        // 세부 정보를 사용하여 정보 창 열기
                        String placeName = place.getName();
                        String placeAddress = place.getAddress();

                        // 정보 창을 사용자에게 표시 (사용자 정의 방식으로 표시할 수 있습니다)
                        showPlaceInfoDialog(placeName, placeAddress);
                    }).addOnFailureListener((exception) -> {
                        // 요청이 실패한 경우 처리
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                    });

                    // 마커 클릭 이벤트 소비 (기본 정보 창이 열리지 않도록 함)
                    return true;
                } else {
                    // 일반 마커인 경우
                    showMarkerInfoDialog(marker);
                    return true; // true 반환 시 마커 정보 창이 표시됩니다.
                }
            }
            private void showPlaceInfoDialog(String placeName, String placeAddress) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(placeName)
                        .setMessage("주소: " + placeAddress)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showMarkerInfoDialog(Marker marker) {
        // 마커 클릭 시 정보 다이얼로그 표시
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(marker.getTitle())
                .setMessage(" ")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 확인 버튼을 클릭하면 다이얼로그를 닫습니다.
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        selectedPlace = place.getLatLng();
        mMap.clear(); // 이전 마커 제거
        Marker marker = mMap.addMarker(new MarkerOptions().position(selectedPlace).title(place.getName()));
        marker.setTag(place.getId()); // 플레이스 ID를 마커의 태그로 설정
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedPlace, 17));
    }

    @Override
    public void onError(@NonNull com.google.android.gms.common.api.Status status) {
        Snackbar.make(requireView(), "Error: " + status.getStatusMessage(), Snackbar.LENGTH_SHORT).show();
    }
}