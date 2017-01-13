package cy.com.morefan;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import com.cy.libapkpatch.PatchUtils;
import cy.com.morefan.constant.Constant;
import cy.com.morefan.service.BaseService;
import cy.com.morefan.util.L;
import cy.com.morefan.util.SecurityUtil;
import cy.com.morefan.view.CustomDialog;
import cy.com.morefan.view.TasksCompletedView;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 客户端升级
 * @author edushi
 *
 */
public class AppUpdateActivity extends BaseActivity{

	private TasksCompletedView mTasksView;
	private String softwarePath = null;
	private ClientDownLoadTask task;
	private boolean isCancel;
	private boolean taskIsComplete;
	private String destMd5;
	private String tips;
	private boolean isForce;
	/**
	 * 整包更新，增量更新
	 * @author edushi
	 *
	 */
	public enum UpdateType{
		FullUpate, DiffUpdate
	}
	private UpdateType updateType;
//	private String PATH = Environment.getExternalStorageDirectory()
//			+ File.separator;
	private String oldapk_filepath ;//"WeiboV3.apk";
	private String newapk_savepath ;//"WeiboV4.1.apk";
	//private String patchpath = "patch.apk";//"weibopatch.apk";
	private String clientURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);


		setContentView(R.layout.appupdate);
		LinearLayout lay = (LinearLayout) findViewById(R.id.lay);
		lay.getBackground().setAlpha(150);

		oldapk_filepath = Constant.PATH_PKG_TEMP  + File.separator + getPackageName() + "_old.apk";
		newapk_savepath = Constant.PATH_PKG_TEMP + File.separator + getPackageName() + "_new.apk";
		softwarePath = Constant.PATH_PKG_TEMP + File.separator + getPackageName() + "_patch.apk";
//		oldapk_filepath = Constant.BASE_IMAGE_PATH  + File.separator + getPackageName() + "_old.apk";
//		newapk_savepath = Constant.BASE_IMAGE_PATH + File.separator + getPackageName() + "_new.apk";
//		softwarePath = Constant.BASE_IMAGE_PATH + File.separator + getPackageName() + "_patch.apk";


		isCancel = false;
		taskIsComplete = false;
		initView();

		Bundle extra = getIntent().getExtras();
		if(extra != null){
			//start download
			File file = new File(softwarePath);
			if(!file.exists())
				file.mkdirs();
			clientURL = extra.getString("url");
			updateType = (UpdateType) extra.getSerializable("type");
			isForce = extra.getBoolean("isForce");
			destMd5 = extra.getString("md5");
			tips = extra.getString("tips");
			TextView txtTips = (TextView) findViewById(R.id.txtTips);
			txtTips.setText(tips);

			task = new ClientDownLoadTask();
			task.execute(clientURL);
		}


	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			CustomDialog.showChooiceDialg(this, "提示", "确定要取消更新吗？", "继续更新", "取消更新", null,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(taskIsComplete){
								downloadClientSuccess();
							}

						}
					},
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(taskIsComplete){
								downloadClientFailed();
							}else{
								//stop task
								isCancel = true;
								//delete file
								File file = new File(softwarePath);
								if(file.exists())
									file.delete();
								//finish();
							}

						}
					});

		}
		return super.onKeyDown(keyCode, event);
	}


	private void initView() {
		mTasksView = (TasksCompletedView) findViewById(R.id.tasks_view);
	}
	private void downloadClientFailed(){

		Toast.makeText(this, "新版本更新失败...", Toast.LENGTH_SHORT).show();
		Intent intent = getIntent();
		intent.putExtra("isForce", isForce);
		setResult(Constant.RESULT_CODE_CLIENT_DOWNLOAD_FAILED, intent);
		finish();

	}
	private boolean backupApk(String srcPkgName, String destPkgName){
		if (TextUtils.isEmpty(srcPkgName) || TextUtils.isEmpty(destPkgName)) {
			return false;
		}
		// check file /data/app/appId-1.apk exists
		File apkFile = new File(srcPkgName);
		if (!apkFile.exists() ) {
			return false;
		}
		FileInputStream in = null;
		try {
			in = new FileInputStream(apkFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		// create dest folder if necessary
		int i = destPkgName.lastIndexOf('/');
		if (i != -1) {
			File dirs = new File(destPkgName.substring(0, i));
			dirs.mkdirs();
			dirs = null;
		}
		// do file copy operation
		byte[] c = new byte[1024];
		int slen;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(destPkgName);
			while ((slen = in.read(c, 0, c.length)) != -1)
				out.write(c, 0, slen);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}
	//版本下载成功，开始启动安装
		private void downloadClientSuccess(){
			if(updateType == UpdateType.FullUpate){//整包更新,直接进行安装
				installApk(softwarePath);
			}else{//增量更新
				new Thread(){
					public void run() {
						//将本地中的apk拷贝一份至临时文件夹
						backupApk(getPackageResourcePath(), oldapk_filepath);
						//step1.patch包与旧包生成新包，并生成新包md5
						L.i("patch start!");
						PatchUtils.patch(oldapk_filepath, newapk_savepath, softwarePath);
						L.i("patch end!");
						//step2.检测md5值是否一致
						String patchMd5 = SecurityUtil.md5sum(newapk_savepath);
						if(destMd5.equals(patchMd5)){//step3.2.md5一致，进行安装
							installApk(newapk_savepath);
						}else{//step3.1.md5不一致重新下载，进行整包更新
							//T.show(AppUpdateActivity.this, "重新更新");
							handler.post(new Runnable() {
								@Override
								public void run() {
									CustomDialog.showChooiceDialg(AppUpdateActivity.this, "温馨提示", "验证错误!更新失败，是否重新更新?", "重新更新", "取消", null,
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													task = new ClientDownLoadTask();
													task.execute(clientURL);
												}
											}, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													//下载失败
													downloadClientFailed();
												}
											});
								}
							});
						}
					};
				}.start();
			}

		}
	private void installApk(String path){

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
			Intent intent =  new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			Uri contentUri = FileProvider.getUriForFile(AppUpdateActivity.this, BuildConfig.APPLICATION_ID + ".provider", new File( path));
			intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
			startActivityForResult(intent, 0);
		}else{
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(path)),	"application/vnd.android.package-archive");
			startActivityForResult(intent, 0);
		}

	}

	class ClientDownLoadTask extends AsyncTask<String, Integer, Integer>{
		private ClientDownLoadHttpService service;
		public ClientDownLoadTask(){
			service = new ClientDownLoadHttpService();
		}
		@Override
		protected Integer doInBackground(String... params) {
			HttpURLConnection hc = service.download(params[0]);

			if(hc != null){
				InputStream update_is = null;
				BufferedInputStream update_bis = null;
				FileOutputStream update_os = null;
				BufferedOutputStream update_bos = null;
				byte[] buffer = null;
				try{
					if(hc.getResponseCode() != 200){
						return null;
					}
					int contentLen = hc.getContentLength();
					if(contentLen == 0){
						return null;
					}
					update_is = hc.getInputStream();
					update_bis = new BufferedInputStream(update_is, 2048);

					File cityMapFile = new File(softwarePath);
					if(cityMapFile.exists()){
						cityMapFile.delete();
//						//删除增量更新的残余文件
//						try {
//							ImageUtil.deleteFileByPath(Constant.PATH_PKG_TEMP);
//						} catch (Exception e) {
//							System.out.println(e.toString());
//						}
					}
						//

					cityMapFile.createNewFile();

					update_os = new FileOutputStream(cityMapFile,false);
					update_bos = new BufferedOutputStream(update_os, 2048);

					buffer = new byte[2048];
					int readed = 0;
					int step = 0;
					while((step = update_bis.read(buffer)) != -1 && !isCancel){
						readed += step;
						update_bos.write(buffer,0,step);
						update_bos.flush();
						publishProgress((int) ((readed / (float)contentLen) * 100), readed ,contentLen);
					}
					update_os.flush();
					return contentLen;
				}catch(IOException e){
					e.printStackTrace();
					return null;
				}finally{
					try{
						if(update_bis != null)
							update_bis.close();
						if(update_is != null)
							update_is.close();
						if(update_bos != null)
							update_bos.close();
						if(update_os != null)
							update_os.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}

			}else{
				//下载失败
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			String des = String.format("%s/%s", formatterSize(values[1]), formatterSize(values[2]));
			mTasksView.setProgress(values[0], des);
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			taskIsComplete = true;
			if(result != null && !isCancel){
				String des = String.format("%s/%s", formatterSize(result), formatterSize(result));
				mTasksView.setProgress(100, des);
				//下载完成
				downloadClientSuccess();
			}else{
				//下载失败
				downloadClientFailed();
			}
		}
	}
	private String formatterSize(int size){
		return Formatter.formatFileSize(AppUpdateActivity.this, size);
	}

	class ClientDownLoadHttpService extends BaseService{
		HttpURLConnection download(String url){
			try{
				HttpURLConnection hc = openMyConnection(url);
				return hc;
			}catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}


}
