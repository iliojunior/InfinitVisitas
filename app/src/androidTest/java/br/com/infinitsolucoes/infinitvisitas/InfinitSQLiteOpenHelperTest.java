package br.com.infinitsolucoes.infinitvisitas;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.InfinitSQLiteOpenHelper;
import br.com.infinitsolucoes.infinitvisitas.Models.ColumnsDB;

@RunWith(AndroidJUnit4.class)
public class InfinitSQLiteOpenHelperTest {
    private static final String TAG = "InfinitSQLiteTest";

    private final class internalClass extends InfinitSQLiteOpenHelper {
        public internalClass() {
            super(InstrumentationRegistry.getContext());
        }

        @NotNull
        @Override
        protected String onGetTable() {
            return "UnitTest";
        }

        @NotNull
        @Override
        protected List<ColumnsDB> onGetColumns() {
            ColumnsDB id = new ColumnsDB("_id", "integer");
            id.setPrimaryKey(true);
            id.setIdentity(true);
            id.setAutoIncrement(true);
            return Arrays.asList(id, new ColumnsDB("teste", "text"));
        }
    }

    @Test
    public void testDatabase() throws Exception {
        internalClass classe = new internalClass();
        Log.d(TAG, classe.getCreateQuery());
        Log.d(TAG, classe.getDropQuery());
    }
}
