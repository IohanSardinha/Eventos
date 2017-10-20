package br.com.sardinha.iohan.eventos;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import br.com.sardinha.iohan.eventos.Activity.LoadingActivity;
import br.com.sardinha.iohan.eventos.Activity.MainActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FirebaseMessagingServic";

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0)
        {
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();

            try
            {
                JSONObject data = new JSONObject(remoteMessage.getData());
                String eventID = data.getString("eventID");
                String userID = data.getString("userID");
                sendNotification(title,message,click_action,eventID,userID);
            }
            catch (Exception ex)
            {
                sendNotification(title,message,"ERROR","","");
            }
        }
    }

    @Override
    public void onDeletedMessages() {

    }

    private void sendNotification(String title,String messageBody,String clickAction,String eventID,String userID) {
        Intent intent;

        if(clickAction.equals("NOVO_EVENTO")){
            intent = new Intent(this,LoadingActivity.class);
            intent.putExtra("eventID",eventID);
            intent.putExtra("userID",userID);
            intent.putExtra("actionType","NOVO_EVENTO");
        }
        if(clickAction.equals("SEGUIDO"))
        {
            intent = new Intent(this,LoadingActivity.class);
            intent.putExtra("eventID",eventID);
            intent.putExtra("userID",userID);
            intent.putExtra("actionType","SEGUIDO");
        }
        else {
            intent = new Intent(this,MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* Request code*/,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setColor(getColor(R.color.colorPrimary))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
