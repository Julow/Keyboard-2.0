package juloo.keyboard2;

import android.content.res.XmlResourceParser;
import java.util.ArrayList;
import java.util.List;

class KeyboardData
{
  public final List<Row> rows;
  /* Total width of the keyboard. Unit is abstract. */
  public final float keysWidth;
  /* Total height of the keyboard. Unit is abstract. */
  public final float keysHeight;

  public KeyboardData(List<Row> rows_)
  {
    float kw = 0.f;
    float kh = 0.f;
    for (Row r : rows_)
    {
      kw = Math.max(kw, r.keysWidth);
      kh += r.height + r.shift;
    }
    rows = rows_;
    keysWidth = kw;
    keysHeight = kh;
  }

  public static KeyboardData parse(XmlResourceParser parser)
  {
    ArrayList<Row> rows = new ArrayList<Row>();

    try
    {
      int status;

      while (parser.next() != XmlResourceParser.START_TAG)
        continue ;
      if (!parser.getName().equals("keyboard"))
        throw new Exception("Unknow tag: " + parser.getName());
      while ((status = parser.next()) != XmlResourceParser.END_DOCUMENT)
      {
        if (status == XmlResourceParser.START_TAG)
        {
          String tag = parser.getName();
          if (tag.equals("row"))
            rows.add(Row.parse(parser));
          else
            throw new Exception("Unknow keyboard tag: " + tag);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return new KeyboardData(rows);
  }

  public KeyboardData removeKeys(MapKeys f)
  {
    ArrayList<Row> rows_ = new ArrayList<Row>();
    for (Row r : rows)
      rows_.add(r.removeKeys(f));
    return new KeyboardData(rows_);
  }

  public static class Row
  {
    public final List<Key> keys;
    /* Height of the row. Unit is abstract. */
    public final float height;
    /* Extra empty space on the top. */
    public final float shift;
    /* Total width of very keys. Unit is abstract. */
    private final float keysWidth;

    public Row(List<Key> keys_, float h, float s)
    {
      float kw = 0.f;
      for (Key k : keys_) kw += k.width + k.shift;
      keys = keys_;
      height = h;
      shift = s;
      keysWidth = kw;
    }

    public static Row parse(XmlResourceParser parser) throws Exception
    {
      ArrayList<Key> keys = new ArrayList<Key>();
      int status;
      float h = parser.getAttributeFloatValue(null, "height", 1f);
      float shift = parser.getAttributeFloatValue(null, "shift", 0f);
      while ((status = parser.next()) != XmlResourceParser.END_TAG)
      {
        if (status == XmlResourceParser.START_TAG)
        {
          String tag = parser.getName();
          if (tag.equals("key"))
            keys.add(Key.parse(parser));
          else
            throw new Exception("Unknow row tag: " + tag);
        }
      }
      return new Row(keys, h, shift);
    }

    public Row removeKeys(MapKeys f)
    {
      ArrayList<Key> keys_ = new ArrayList<Key>();
      for (Key k : keys)
        keys_.add(k.removeKeys(f));
      return new Row(keys_, height, shift);
    }
  }

  public static class Key
  {
    /*
     ** 1   2
     **   0
     ** 3   4
     */
    public final KeyValue key0;
    public final KeyValue key1;
    public final KeyValue key2;
    public final KeyValue key3;
    public final KeyValue key4;

    /* Key width in relative unit. */
    public final float width;
    /* Extra empty space on the left of the key. */
    public final float shift;

    public Key(KeyValue k0, KeyValue k1, KeyValue k2, KeyValue k3, KeyValue k4, float w, float s)
    {
      key0 = k0;
      key1 = k1;
      key2 = k2;
      key3 = k3;
      key4 = k4;
      width = w;
      shift = s;
    }

    public static Key parse(XmlResourceParser parser) throws Exception
    {
      KeyValue k0 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key0"));
      KeyValue k1 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key1"));
      KeyValue k2 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key2"));
      KeyValue k3 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key3"));
      KeyValue k4 = KeyValue.getKeyByName(parser.getAttributeValue(null, "key4"));
      float width = parser.getAttributeFloatValue(null, "width", 1f);
      float shift = parser.getAttributeFloatValue(null, "shift", 0.f);
      while (parser.next() != XmlResourceParser.END_TAG)
        continue ;
      return new Key(k0, k1, k2, k3, k4, width, shift);
    }

    public Key removeKeys(MapKeys f)
    {
      return new Key(f.map(key0), f.map(key1), f.map(key2), f.map(key3), f.map(key4), width, shift);
    }
  }

  public static abstract interface MapKeys
  {
    public abstract KeyValue map(KeyValue k);
  }

  public static class RemoveKeysByFlags implements MapKeys
  {
    private final int _flags;

    public RemoveKeysByFlags(int flags) { _flags = flags; }

    public KeyValue map(KeyValue k)
    {
      return (k == null || (k.flags & _flags) != 0) ? null : k;
    }
  }

  public static class RemoveKeysByEvent implements MapKeys
  {
    private final int _eventCode;

    public RemoveKeysByEvent(int ev) { _eventCode = ev; }

    public KeyValue map(KeyValue k)
    {
      return (k == null || k.eventCode == _eventCode) ? null : k;
    }
  }
}
