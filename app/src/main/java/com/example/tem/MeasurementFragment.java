package com.example.tem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tem.Measurement.ResultActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MeasurementFragment extends Fragment {

    private MainActivity mainActivity;
    private static final String TAG = "MeasurementFragment";
    private ListView listView;

    public MeasurementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurement, container, false);
    }

    public class FileAdapter extends ArrayAdapter<File> {

        private int resourceId;

        public FileAdapter(@NonNull Context context, int resource, @NonNull List<File> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            File file=getItem(position);
            String fileName=file.getName().toString();
            long fileSize=file.length()/1024;
            long time=file.lastModified();
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
            String modfiedTime=formatter.format(time);
            View view =LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            TextView textView = (TextView) view.findViewById(R.id.file_info);
            textView.setText(fileName+"\n"+modfiedTime+"  "+fileSize+"K");
            if (file.isDirectory())
                ((ImageView) view.findViewById(R.id.file_image)).setImageResource(R.drawable.image_directory);
            return view;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.fragmentState=3;
        mainActivity = (MainActivity) getActivity();
        List<File> fileList = new ArrayList<File>();//向上转型
        List<File> fileListtemp = new ArrayList<File>();
        File dir = new File(Constants.DATA_DIRECTORY);
        if (!dir.exists())//若文件夹不存在则创建文件夹
        { boolean y=dir.mkdirs();
            if(y)
                Log.e(TAG, "创建文件夹 " );
            else  Log.e(TAG, "创建文件夹失败 " );}
        File[] files = dir.listFiles();//listFiles()方法是返回某个目录下所有文件和目录的绝对路径，返回的是File数组
        if (files != null)
            Collections.addAll(fileListtemp, files);//将files中的元素添加到fileList集合中
        //Log.e(TAG, Constants.DATA_DIRECTORY+":"+fileList.toString());
/*
        for (File f : fileList)//若目录中存在文件夹则删除文件夹
        {
            if (f.length()<1024)
                fileList.remove(f);
        }
*/
        for(int i=0;i<fileListtemp.size();i++){
            if(fileListtemp.get(i).length()!=0){
                fileList.add(fileListtemp.get(i));
            }
        }
        if (fileList.isEmpty()) {
            SharedPreferences.Editor pre = ((MainActivity) getActivity()).getSharedPreferences("butState", 0).edit();
            pre.putString("DataFilePathName", null);
            pre.apply();
            ((MainActivity) getActivity()).setTabDisplay(4);//加载noneFileFragment
        }
        else {
            listView = (ListView) getActivity().findViewById(R.id.datafragment_list_view);
            FileAdapter adapter = new FileAdapter(
                    getActivity(), R.layout.file_list_item, fileList);//将文件列表fileList添加到listView中
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //todo
                    ParamSaveClass.Location=fileList.get(position).getName();
                    Log.e(TAG, "ParamSaveClass.Location"+ParamSaveClass.Location+"position  "+position);
                    String location= ((TextView) view.findViewById(R.id.file_info)).getText().toString();
                    String[] strArray = location.split("\\r?\\n");//分割换行
                    Log.e(TAG, "strArray:"+ Arrays.toString(strArray));
                    location=strArray[0];
                    Intent intent = new Intent(getActivity(), ResultActivity.class);
                    intent.putExtra("location", location);//使用intent传递数据
                    startActivity(intent);
                    Log.e(TAG, "location:"+location);
                    /*String DataFilePathname= Constants.DATA_DIRECTORY+"/"
                            +((TextView) view.findViewById(R.id.file_info)).getText().toString().split("\n")[0];
                    String[] strArray = DataFilePathname.split("\\.");
                    int suffixIndex = strArray.length -1;
                    if (strArray[suffixIndex].equals("dat")) {
                        SharedPreferences.Editor pre = ((MainActivity) getActivity()).getSharedPreferences("butState", 0).edit();
                        pre.putString("DataFilePathName", DataFilePathname);
                        pre.apply();
                       *//*Intent intent=new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("DataFilePathName", DataFilePathname);     //装入数据
                        intent.putExtras(bundle);*//*
                        ((MainActivity) getActivity()).setTabDisplay(0);
                    }*/
                }
            });
        }
    }
}
