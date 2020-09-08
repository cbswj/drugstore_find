package com.school_drugstore;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class drugstore_MyAdapter extends BaseAdapter {

    /* 아이템을 세트로 담기 위한 어레이 */
    private ArrayList<drugstore_MyItem> mItems = new ArrayList<>();

    String key;


    public static class ViewHolder {
        TextView list_tag;
        TextView list_title;
        TextView list_day;
        ImageView list_star;
    }

    ViewHolder holder;


    private tmapClickListener tmap_mListener = null; //댓글 클릭 이벤트 리스터

    public interface tmapClickListener{ void onItemClick(View v, int pos);}  // 클릭 동작 이벤트 오버라이딩

    public void tmap_setOnItemClickListener(tmapClickListener listener){ //댓글 클릭 리스너 설정
        this.tmap_mListener = listener;
    }

    public ArrayList<drugstore_MyItem>  getmItems(){
        return mItems;
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public drugstore_MyItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //ListView 가 각각의 list item 을 그릴때 Adapter에게 어떻게 그려야 할 지 묻는다. 그때 사용하는 함수가 getView()


    // LIstView에 보여지게 되는 데이터인 Voca 객체 리스트의 인덱스

    // 주어진 데이터를 보여주기 위해 사용될 한 줄(row)을 위한 뷰(View)
    // 값이 null인 경우에만 새로 생성하고 그 외에는 재사용됩니다.

    // XML 레이아웃 파일을 View 객체로 변환하기 위해 사용되는 부모 뷰그룹

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Context context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drugstore_listview, parent, false);

            holder = new ViewHolder();
            holder.list_tag = (TextView) convertView.findViewById(R.id.list_tag);
            holder.list_title = (TextView) convertView.findViewById(R.id.list_title);
            holder.list_day = (TextView) convertView.findViewById(R.id.list_day);
            holder.list_star = (ImageView) convertView.findViewById(R.id.list_star);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */



        /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
        final drugstore_MyItem myItem = getItem(position);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        holder.list_tag.setText(myItem.getAddress());
        holder.list_title.setText(myItem.getTitle());
        holder.list_day.setText("TEL)");
        holder.list_day.append(myItem.getTel());
        holder.list_star.setImageResource(myItem.getStar());

        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

       // getFirebaseDatabase2();



        holder.list_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tmap_mListener!=null){
                    tmap_mListener.onItemClick(v,position);
                }
            }
        });


        return convertView;
    }


    public void addAll(ArrayList<drugstore_MyItem> array){
        mItems.addAll(array);
    }




}