package de.fh_dortmund.beerbuddy_44.dao.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.LinkedList;
import java.util.List;

import de.fh_dortmund.beerbuddy.entities.FriendInvitation;
import de.fh_dortmund.beerbuddy.entities.FriendList;
import de.fh_dortmund.beerbuddy.entities.Person;
import de.fh_dortmund.beerbuddy_44.acitvitys.BeerBuddyActivity;
import de.fh_dortmund.beerbuddy_44.dao.interfaces.FriendInvitationDAO;
import de.fh_dortmund.beerbuddy_44.dao.interfaces.FriendListDAO;
import de.fh_dortmund.beerbuddy_44.dao.util.BeerBuddyDbHelper;
import de.fh_dortmund.beerbuddy_44.exceptions.DataAccessException;

/**
 * Created by David on 30.11.2015.
 */
public class FriendInvitationDAOLocal extends FriendInvitationDAO {

    BeerBuddyDbHelper dbHelper;

    public FriendInvitationDAOLocal(BeerBuddyActivity context) {
        super(context);
        dbHelper = BeerBuddyDbHelper.getInstance(context);
    }

    @Override
    public void insertOrUpdate(FriendInvitation i, RequestListener<FriendInvitation> listener) {
        try {
            if (i.getId() != 0) {
                listener.onRequestSuccess(update(i));
            } else {
                listener.onRequestSuccess(insert(i));
            }
        } catch (DataAccessException e) {
            listener.onRequestFailure(new SpiceException(e));
        }

    }

    private FriendInvitation insert(FriendInvitation i) throws DataAccessException {
        SQLiteDatabase database = dbHelper.getDatabase();
        try {
            SQLiteStatement stmt = database.compileStatement("INSERT INTO friendinvitation (einladerId,eingeladenerId,freitext,version) VALUES (?,?,?,?)");
            stmt.bindLong(1, i.getEinladerId());
            stmt.bindLong(2, i.getEingeladenerId());
            if(i.getFreitext()!=null)
            stmt.bindString(3, i.getFreitext());
            stmt.bindLong(4, i.getVersion());
            i.setId( stmt.executeInsert());
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to insert or update DrinkingInvitation", e);
        } finally {
            database.close();
        }
    }

    private FriendInvitation update(FriendInvitation i) throws DataAccessException {
        SQLiteDatabase database = dbHelper.getDatabase();
        try {
            SQLiteStatement stmt = database.compileStatement("UPDATE  friendinvitation SET einladerId = ? ,  eingeladenerId=?,freitext=?,version=?) WHERE id = ?  ");
            stmt.bindLong(1, i.getEinladerId());
            stmt.bindLong(2, i.getEingeladenerId());
            if(i.getFreitext()!=null)
                stmt.bindString(3, i.getFreitext());

            stmt.bindLong(4, i.getVersion());
            stmt.bindLong(5, i.getId());
            stmt.executeUpdateDelete();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to insert or update DrinkingInvitation", e);
        } finally {
            database.close();
        }
    }

    public void delete(FriendInvitation i) throws DataAccessException {
        SQLiteDatabase database = dbHelper.getDatabase();
        try {
            SQLiteStatement stmt = database.compileStatement("DELETE FROM friendinvitation WHERE id = ?  ");
            stmt.bindLong(1, i.getId());
            stmt.executeUpdateDelete();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to delete FriendInvitation", e);
        } finally {
            database.close();
        }
    }


    private FriendInvitation getById(long id) throws DataAccessException {
        SQLiteDatabase database = dbHelper.getDatabase();
        Cursor dbCursor = null;

        try {
            dbCursor = database.query("drinkinginvitation", new String[]{"id", "einladerId", "drinkingSpotId", "eingeladenerId", "freitext", "version"}, " id = ?", new String[]{id + ""}, null, null, null);
            List<FriendInvitation> list = new LinkedList<FriendInvitation>();
            while (dbCursor.moveToNext()) {
                return getFriendInvitatio(dbCursor);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Failed to insert or update DrinkingInvitation", e);
        } finally {
            if (dbCursor != null) {
                dbCursor.close();
            }
            database.close();
        }
    }

    @Override
    public void getAllFor(long personid, RequestListener<FriendInvitation[]> listener) {
        SQLiteDatabase database = dbHelper.getDatabase();
        Cursor dbCursor = null;

        try {
            dbCursor = database.query("friendinvitation", new String[]{"id", "einladerId", "eingeladenerId", "freitext", "version"}, " eingeladenerId = ?", new String[]{personid + ""}, null, null, null);
            List<FriendInvitation> list = new LinkedList<FriendInvitation>();
            while (dbCursor.moveToNext()) {
                FriendInvitation di = getFriendInvitatio(dbCursor);
                list.add(di);
            }
            listener.onRequestSuccess(list.toArray(new FriendInvitation[]{}));
        } catch (Exception e) {
            e.printStackTrace();
            listener.onRequestFailure(new SpiceException(e));
        } finally {
            if (dbCursor != null) {
                dbCursor.close();
            }
            database.close();
        }
    }

    private FriendInvitation getFriendInvitatio(Cursor dbCursor) {
        FriendInvitation di = new FriendInvitation();
        di.setId(dbCursor.getLong(dbCursor.getColumnIndex("id")));
        di.setEinladerId(dbCursor.getLong(dbCursor.getColumnIndex("einladerId")));
        di.setEingeladenerId(dbCursor.getLong(dbCursor.getColumnIndex("eingeladenerId")));
        di.setVersion(dbCursor.getLong(dbCursor.getColumnIndex("version")));
        di.setFreitext(dbCursor.getString(dbCursor.getColumnIndex("freitext")));
        return di;
    }


    @Override
    public void getAllFrom(long personid, RequestListener<FriendInvitation[]> listener) {
        SQLiteDatabase database = dbHelper.getDatabase();
        Cursor dbCursor = null;

        try {
            dbCursor = database.query("friendinvitation", new String[]{"id", "einladerId", "eingeladenerId", "freitext", "version"}, " einladerId = ?", new String[]{personid + ""}, null, null, null);
            List<FriendInvitation> list = new LinkedList<FriendInvitation>();
            while (dbCursor.moveToNext()) {
                list.add(getFriendInvitatio(dbCursor));
            }
            listener.onRequestSuccess(list.toArray(new FriendInvitation[]{}));
        } catch (Exception e) {
            e.printStackTrace();
            listener.onRequestFailure(new SpiceException(e));
        } finally {
            if (dbCursor != null) {
                dbCursor.close();
            }
            database.close();
        }
    }

    @Override
    public void accept(final FriendInvitation friendInvitation, final RequestListener<Void> listener) {
        try {
            accept(friendInvitation);
            listener.onRequestSuccess(null);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onRequestFailure(new SpiceException(e));
        }
    }

    public void accept(FriendInvitation friendInvitation) throws DataAccessException{
        FriendListDAOLocal friendListDAOLocal = new FriendListDAOLocal(context);
        FriendList friendList = friendListDAOLocal.getFriendList(friendInvitation.getEingeladenerId());
        FriendList friendList1 = friendListDAOLocal.getFriendList(friendInvitation.getEinladerId());
        if(friendList == null)
        {
            friendList = new FriendList();
            friendList.setPersonid(friendInvitation.getEingeladenerId());
        }

        if(friendList1 == null)
        {
            friendList1 = new FriendList();
            friendList1.setPersonid(friendInvitation.getEinladerId());
        }
        friendList.getFriends().add(new Person(friendInvitation.getEinladerId()));
        friendList1.getFriends().add(new Person(friendInvitation.getEingeladenerId()));
        friendListDAOLocal.insertOrUpdate(friendList);
        friendListDAOLocal.insertOrUpdate(friendList1);
        delete(friendInvitation);
    }

    @Override
    public void decline(FriendInvitation invitation, RequestListener<Void> listener) {
        try {
            delete(invitation);
            listener.onRequestSuccess(null);
        } catch (DataAccessException e) {
            e.printStackTrace();
            listener.onRequestFailure(new SpiceException(e));
        }

    }


}
