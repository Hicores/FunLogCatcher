/*
 *                               _ooOoo_
 *                              o8888888o
 *                              88" . "88
 *                              (| -_- |)
 *                              O\  =  /O
 *                           ____/`- -'\____
 *                         .'  \\|     |//  `.
 *                        /  \\|||  :  |||//  \
 *                       /  _||||| -:- |||||-  \
 *                       |   | \\\  -  /// |   |
 *                       | \_|  ''\- -/''  |   |
 *                       \  .-\__  `-`  ___/-. /
 *                     ___`. .'  / -.- \  `. . __
 *                  ."" '<  `.___\_<|>_/___.'  >'"".
 *                 | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                 \  \ `-.   \_ __\ /__ _/   .-` /  /
 *            ======`-.____`-.___\_____/___.-`____.-'======
 *                               `=- -='
 *            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *                       佛祖保佑        永无BUG
 *              佛曰:
 *                     写字楼里写字间，写字间里程序员；
 *                     程序人员写程序，又拿程序换酒钱。
 *                     酒醒只在网上坐，酒醉还来网下眠；
 *                     酒醉酒醒日复日，网上网下年复年。
 *                     但愿老死电脑间，不愿鞠躬老板前；
 *                     奔驰宝马贵者趣，公交自行程序员。
 *                     别人笑我忒疯癫，我笑自己命太贱；
 *                     不见满街漂亮妹，哪个归得程序员？
 */

package fun.logcatcher.ui;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fun.logcatcher.R;

public class XListDialog {
    public static XListDialog build(Context context){
        return new XListDialog(context);
    }
    public static class XListItemData{
        public String title;
        public String packageName;
        public String tag;
        public boolean checkStatus;
        public Object extra;
    }
    private Context context;
    private String title;
    private List<XListItemData> data;
    private XDialogClick clickEvent;
    private boolean show_search;
    private boolean isCheck;
    private XCall<List<XListItemData>> dismissCall;
    public interface XDialogClick{
        void onClick(XListItemData tag);
    }
    private XListDialog(Context context){
        this.context = context;
    }
    public XListDialog title(String str){
        this.title = str;
        return this;
    }
    public XListDialog data(List<XListItemData> data){
        this.data = data;
        return this;
    }
    public XListDialog event(XDialogClick click){
        this.clickEvent = click;
        return this;
    }
    public XListDialog dismiss(XCall<List<XListItemData>> call){
        this.dismissCall = call;
        return this;
    }
    public XListDialog show_search(){
        this.show_search = true;
        return this;
    }
    public XListDialog setChecked(){
        this.isCheck = true;
        return this;
    }
    public void show(){
        Dialog dialog = new Dialog(context);
        ViewGroup root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.xlist_dialog_root,null);
        TextView tv_title = root.findViewById(R.id.xlist_root_title);
        LinearLayout content = root.findViewById(R.id.xlist_root_content);
        ScrollView scrollView = root.findViewById(R.id.xlist_root_content_scroll);
        TextView tv_btn = root.findViewById(R.id.xlist_root_btn);
        EditText ed_search = root.findViewById(R.id.xlist_root_search_edit);

        if(!show_search){
            ed_search.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) scrollView.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.xlist_root_title);
            scrollView.setLayoutParams(params);
        }

        tv_title.setText(title);

        List<View> searchItems = new ArrayList<>();

        if (isCheck){
            for (XListItemData item : data){
                View itemView = LayoutInflater.from(context).inflate(R.layout.xlist_dialog_content_check,null);
                CheckBox tv_item = itemView.findViewById(R.id.xlist_dialog_content_item_title);
                tv_item.setText(item.title);
                tv_item.setChecked(item.checkStatus);
                tv_item.setTag(item.title);
                tv_item.setOnCheckedChangeListener((buttonView, isChecked) -> item.checkStatus = isChecked);
                content.addView(itemView);
                searchItems.add(itemView);
            }
            tv_btn.setOnClickListener(v -> {
                if (clickEvent != null){
                    clickEvent.onClick(null);
                }
                dialog.dismiss();
            });
        }else {
            for (XListItemData item : data){
                View itemView = LayoutInflater.from(context).inflate(R.layout.xlist_dialog_content_item,null);
                TextView tv_item = itemView.findViewById(R.id.xlist_dialog_content_item_title);
                TextView tv_package = itemView.findViewById(R.id.xlist_dialog_content_item_package);
                tv_item.setText(item.title);
                tv_item.setTag((item.tag ).toLowerCase());
                tv_package.setText(item.packageName);
                itemView.setOnClickListener(v -> {
                    if (clickEvent != null){
                        clickEvent.onClick(item);
                    }
                    dialog.dismiss();
                });
                content.addView(itemView);
                searchItems.add(itemView);
            }

            tv_btn.setVisibility(View.GONE);
        }


        ed_search.setSingleLine();
        ed_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = s.toString().toLowerCase();
                for (View itemView : searchItems){
                    View tv_item = itemView.findViewById(R.id.xlist_dialog_content_item_title);
                    Object tag = (Object) tv_item.getTag();
                    if (tag instanceof String){
                        if (((String)tag).contains(search)) {
                            itemView.setVisibility(View.VISIBLE);
                        }else {
                            itemView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });


        Window dialogWindow = dialog.getWindow();
        dialogWindow.setContentView(root);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);
        dialogWindow.setLayout((int) (LayoutUtils.getScreenWidth(context) * 0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setOnDismissListener(dialog1 -> {
            if (dismissCall != null){
                dismissCall.call(data);
            }
        });
        dialog.show();




    }
}
