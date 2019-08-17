package com.nkvoronov.tvprogram.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import org.jsoup.select.Elements;
import java.util.Date;
import static com.nkvoronov.tvprogram.common.DateUtils.getFormatDate;
import static com.nkvoronov.tvprogram.common.HttpContent.HOST;

public class ParserVseTV extends Parser {
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";

    public ParserVseTV(Context context, SQLiteDatabase database, int countDay, Boolean fullDesc) {
        super(context, database, countDay, fullDesc);
    }

    @Override
    public void getContent() {
        super.getContent();
    }

    @Override
    public void getContentDay(TVChannel channel, Date date) {
        String vdirection = String.format(STR_SCHEDULECHANNEL, channel.getIndex(), getFormatDate(date, "yyyy-MM-dd"));
        org.jsoup.nodes.Document doc = new HttpContent(HOST + vdirection).getDocument();
        Elements items = doc.select(STR_ELMDOCSELECT);
        for (org.jsoup.nodes.Element item : items){
            //
        }

    }

    @Override
    public void runParser() {
        super.runParser();
    }
}
