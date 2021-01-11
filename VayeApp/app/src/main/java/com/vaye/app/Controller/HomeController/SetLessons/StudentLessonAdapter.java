package com.vaye.app.Controller.HomeController.SetLessons;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vaye.app.Controller.HomeController.PagerAdapter.AllDatasAdapter;
import com.vaye.app.Interfaces.CallBackCount;
import com.vaye.app.Interfaces.TrueFalse;
import com.vaye.app.Model.CurrentUser;
import com.vaye.app.Model.LessonModel;
import com.vaye.app.R;
import com.vaye.app.Services.LessonSettingService;

import java.util.ArrayList;
import java.util.List;

public class StudentLessonAdapter extends RecyclerView.Adapter<StudentLessonAdapter.StudentLessonViewHolder>  implements Filterable {

    ArrayList<LessonModel> list;
    ArrayList<LessonModel> listFilter;
    CurrentUser currentUser;
    Context context;

    public StudentLessonAdapter(ArrayList<LessonModel> list, CurrentUser currentUser, Context context) {
        this.list = list;
        this.currentUser = currentUser;
        this.context = context;
    }

    @NonNull
    @Override
    public StudentLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_search_item, parent, false);

        return new StudentLessonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentLessonViewHolder holder, int i) {


        holder.setFollowersCont(list.get(i).getLessonName(),currentUser);
        holder.setLessonName(list.get(i).getLessonName());
        holder.setAdd_remove(currentUser , list.get(i).getLessonName());

        holder.itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Ders Çıkar",Toast.LENGTH_SHORT).show();

            }
        });
        holder.itemView.findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"Ders Ekle",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LessonModel> newFilteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                newFilteredList.addAll(list);
            }else{
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (LessonModel item : list){
                    if (item.getLessonName().toLowerCase().contains(filterPattern)){
                        newFilteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = newFilteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listFilter.clear();
            listFilter.addAll((List) filterResults.values);
            notifyDataSetChanged();;

        }
    };

    class StudentLessonViewHolder extends RecyclerView.ViewHolder{

        public StudentLessonViewHolder(@NonNull View itemView) {
            super(itemView);

        }

        TextView lessonName = (TextView)itemView.findViewById(R.id.lessonName);
        TextView followers = (TextView)itemView.findViewById(R.id.followersCount);
        ImageButton add = (ImageButton)itemView.findViewById(R.id.add);
        ImageButton remove = (ImageButton)itemView.findViewById(R.id.delete);
        public void setLessonName(String _lessonName){
                lessonName.setText(_lessonName);
        }
        public void setFollowersCont(String _lessonName , CurrentUser currentUser){
            LessonSettingService.shared().getFollowersCont(_lessonName, currentUser, new CallBackCount() {
                @Override
                public void callBackCount(long count) {
                    followers.setText(String.valueOf(count) + " Takipçi");
                }
            });
        }

        public void setAdd_remove(CurrentUser currentUser , String  lessonName){
            LessonSettingService.shared().checkIsFollowing(lessonName, currentUser, new TrueFalse<Boolean>() {
                @Override
                public void callBack(Boolean _value) {
                    if (_value){
                        add.setVisibility(View.GONE);
                        remove.setVisibility(View.VISIBLE);
                    }else{
                        add.setVisibility(View.VISIBLE);
                        remove.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
