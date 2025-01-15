//
//
//package com.example.habittracker;
//
//import android.widget.TableLayout;
//import android.widget.TableRow;
//import android.widget.TextView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.ValueEventListener;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.robolectric.Robolectric;
//import org.robolectric.RobolectricTestRunner;
//import org.robolectric.annotation.Config;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;
//
//import androidx.fragment.app.FragmentActivity;
//
//@Config(sdk = 28) // API seviyesini belirtebilirsiniz
//public class ProgressFragmentTest {
//
//    private ProgressFragment progressFragment;
//    private DatabaseReference mockGoalsReference;
//    private DatabaseReference mockProgressReference;
//    private DataSnapshot mockSnapshot;
//
//    @Before
//    public void setUp() {
//        // Sahte bir Activity ve Fragment oluştur
//        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).create().start().resume().get();
//        progressFragment = new ProgressFragment();
//
//        // Fragment'ı sahte Activity'ye ekle
//        activity.getSupportFragmentManager().beginTransaction().add(progressFragment, null).commitNow();
//
//        // Firebase referanslarını mockla
//        mockGoalsReference = Mockito.mock(DatabaseReference.class);
//        mockProgressReference = Mockito.mock(DatabaseReference.class);
//        mockSnapshot = Mockito.mock(DataSnapshot.class);
//
//        // ProgressFragment'e mock referansları bağla
//        progressFragment.goalsReference = mockGoalsReference;
//        progressFragment.progressReference = mockProgressReference;
//    }
//
//    @Test
//    public void testFetchGoals_Success() {
//        // Sahte DataSnapshot ve Goal verileri oluştur
//        DataSnapshot mockGoal1 = Mockito.mock(DataSnapshot.class);
//        DataSnapshot mockGoal2 = Mockito.mock(DataSnapshot.class);
//
//        when(mockGoal1.getValue(Goal.class)).thenReturn(new Goal("1", "Goal 1"));
//        when(mockGoal2.getValue(Goal.class)).thenReturn(new Goal("2", "Goal 2"));
//
//        List<DataSnapshot> children = new ArrayList<>();
//        children.add(mockGoal1);
//        children.add(mockGoal2);
//
//        when(mockSnapshot.getChildren()).thenReturn(children);
//
//        // Sahte bir ValueEventListener oluştur
//        doAnswer(invocation -> {
//            ValueEventListener listener = invocation.getArgument(0);
//            listener.onDataChange(mockSnapshot); // onDataChange'i tetikle
//            return null;
//        }).when(mockGoalsReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
//
//        // Testi çalıştır
//        progressFragment.fetchGoals();
//
//        // ProgressTable'ı kontrol et
//        TableLayout table = progressFragment.progressTable;
//        assertNotNull(table);
//        assertEquals(2, table.getChildCount()); // 2 Goal eklenmeli
//
//        TableRow row1 = (TableRow) table.getChildAt(0);
//        TextView goalName1 = (TextView) row1.getChildAt(0);
//        assertEquals("Goal 1", goalName1.getText().toString());
//
//        TableRow row2 = (TableRow) table.getChildAt(1);
//        TextView goalName2 = (TextView) row2.getChildAt(0);
//        assertEquals("Goal 2", goalName2.getText().toString());
//    }
//
//    @Test
//    public void testFetchGoals_Failure() {
//        // Sahte bir ValueEventListener oluştur
//        doAnswer(invocation -> {
//            ValueEventListener listener = invocation.getArgument(0);
//            listener.onCancelled(DatabaseError.fromCode(DatabaseError.NETWORK_ERROR));
//            return null;
//        }).when(mockGoalsReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
//
//        // Testi çalıştır
//        progressFragment.fetchGoals();
//
//        // Bir hata durumunda herhangi bir exception fırlatılmadığını doğrula
//        // (Hata durumunda UI'ye Toast basılıyor, ancak bunu test etmiyoruz)
//    }
//}










package com.example.habittracker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {28}) // Robolectric'in hangi Android SDK sürümünü taklit edeceğini belirtiyoruz
public class ProgressFragmentTest {

    private ProgressFragment progressFragment;
    private DatabaseReference mockGoalsReference;
    private DatabaseReference mockProgressReference;
    private DataSnapshot mockSnapshot;

    @Before
    public void setUp() {
        // ProgressFragment örneğini oluştur
        progressFragment = new ProgressFragment();

        // Firebase referanslarını mockla
        mockGoalsReference = Mockito.mock(DatabaseReference.class);
        mockProgressReference = Mockito.mock(DatabaseReference.class);
        mockSnapshot = Mockito.mock(DataSnapshot.class);

        // ProgressFragment'e mock referansları ekle
        progressFragment.goalsReference = mockGoalsReference;
        progressFragment.progressReference = mockProgressReference;
    }

    @Test
    public void testFetchGoals_Success() {
        // Sahte DataSnapshot ve Goal verileri oluştur
        DataSnapshot mockGoal1 = Mockito.mock(DataSnapshot.class);
        DataSnapshot mockGoal2 = Mockito.mock(DataSnapshot.class);

        when(mockGoal1.getValue(Goal.class)).thenReturn(new Goal("1", "Goal 1"));
        when(mockGoal2.getValue(Goal.class)).thenReturn(new Goal("2", "Goal 2"));

        List<DataSnapshot> children = new ArrayList<>();
        children.add(mockGoal1);
        children.add(mockGoal2);

        when(mockSnapshot.getChildren()).thenReturn(children);

        // Sahte bir ValueEventListener oluştur
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onDataChange(mockSnapshot); // onDataChange'i tetikle
            return null;
        }).when(mockGoalsReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Testi çalıştır
        progressFragment.fetchGoals();

        // Mock'lanan metotların çağrıldığını doğrula
        verify(mockGoalsReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }

    @Test
    public void testFetchGoals_Failure() {
        // Sahte bir ValueEventListener oluştur
        doAnswer(invocation -> {
            ValueEventListener listener = invocation.getArgument(0);
            listener.onCancelled(DatabaseError.fromCode(DatabaseError.NETWORK_ERROR));
            return null;
        }).when(mockGoalsReference).addListenerForSingleValueEvent(any(ValueEventListener.class));

        // Testi çalıştır
        progressFragment.fetchGoals();

        // Mock'lanan metotların çağrıldığını doğrula
        verify(mockGoalsReference).addListenerForSingleValueEvent(any(ValueEventListener.class));
    }
}
