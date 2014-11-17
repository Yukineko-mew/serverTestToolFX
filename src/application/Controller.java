package application;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Controller implements Initializable {

	ObservableList<String> adapter;
	JSONObject jObject;

	String responseString;
	
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField key;

	@FXML
	private ListView<String> listView;

	@FXML
	private TextArea response;

	@FXML
	private TextField url;

	@FXML
	private TextArea value;
	
	@FXML
	private TextField jsonName;	

	@FXML
	private ChoiceBox choice;
	
	@FXML
	void addQuery(MouseEvent event) {
		adapter.add(choice.getItems().get(choice.getSelectionModel().getSelectedIndex())
				+ "-- {\"" + key.getText() + "\":\"" + value.getText() + "\"}");

		if(choice.getSelectionModel().getSelectedIndex() == 0) {
			jObject.accumulate(key.getText(), (int)Integer.valueOf(value.getText()));
		} else if(choice.getSelectionModel().getSelectedIndex() == 1) {
			jObject.accumulate(key.getText(), (double)Double.valueOf(value.getText()));
		} else if(choice.getSelectionModel().getSelectedIndex() == 2 ) {
			jObject.accumulate(key.getText(), value.getText());
		} else if(choice.getSelectionModel().getSelectedIndex() == 3) {
			jObject.accumulate(key.getText(), value.getText());
		}
		
		key.setText("");
		value.setText("");
	}

	@FXML
	void sendQuery (MouseEvent event) {
		List <NameValuePair> params = new ArrayList <NameValuePair>();
		params.add( new BasicNameValuePair(jsonName.getText(), jObject.toString()));

		// 送信先URLを指定して通信を確立
		String theUrl="http://"+url.getText();
		HttpPost httpPost = new HttpPost(theUrl);

		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse = null;
		
		try {
		// パラメータの設定
			System.out.println(params.toString());
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));	
			
			System.out.println(httpPost.getURI());
			httpResponse = client.execute(httpPost);
			
			// ステータスコードを取得
			int statusCode = httpResponse.getStatusLine().getStatusCode();

			// レスポンスを取得
			HttpEntity entity = httpResponse.getEntity();
			responseString = EntityUtils.toString(entity);

			// リソースを解放
			EntityUtils.consume(entity);
			
			// クライアントを終了させる
//			client.getConnectionManager().shutdown();
			
			response.setText(params.toString()+"\n"+statusCode+":"+responseString);
			adapter.clear();
			jObject = new JSONObject();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			response.setText(e1.toString());
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			response.setText(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			response.setText(e.toString());
			e.printStackTrace();
		} finally {
			// クライアントを終了させる
			client.getConnectionManager().shutdown();
		}
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		adapter = FXCollections.observableArrayList();
		jObject = new JSONObject();
		listView.setItems(adapter);
	}

}
