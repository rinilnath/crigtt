package gov.hhs.onc.crigtt.validate.vocab.impl;

import com.github.sebhoss.warnings.CompilerWarnings;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import gov.hhs.onc.crigtt.validate.vocab.VocabFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.sitenv.vocabularies.loader.code.rxnorm.RxNormLoader;

public class CrigttRxNormLoader extends RxNormLoader {
    private final static String LINE_DELIM = "|";

    private final static String MEDICATION_CLINICAL_GENERAL_DRUG_VALUE_SET_ID = "2.16.840.1.113883.3.88.12.80.17";
    private final static String MEDICATION_CLINICAL_GENERAL_DRUG_VALUE_SET_NAME = "Medication Clinical General Drug";

    private final static String MEDICATION_CLINICAL_BRAND_DRUG_VALUE_SET_ID = "2.16.840.1.113762.1.4.1010.5";
    private final static String MEDICATION_CLINICAL_BRAND_DRUG_VALUE_SET_NAME = "Medication Clinical Brand-specific Drug";

    private final static String RX_NORM_SRC_ABBR = "RXNORM";

    @SuppressWarnings({ CompilerWarnings.SERIAL })
    private final static Set<String> MEDICATION_CLINICAL_GENERAL_DRUG_TTYS = new HashSet<String>() {
        {
            this.add("SCD");
            this.add("GPCK");
            this.add("SCDG");
            this.add("SCDF");
        }
    };

    @SuppressWarnings({ CompilerWarnings.SERIAL })
    private final static Set<String> MEDICATION_CLINICAL_BRAND_DRUG_TTYS = new HashSet<String>() {
        {
            this.add("SBD");
            this.add("BPCK");
            this.add("SBDG");
            this.add("SBDF");
        }
    };

    @SuppressWarnings({ CompilerWarnings.SERIAL })
    private final static List<String> DRUG_DOC_COPY_FIELD_NAMES = new ArrayList<String>() {
        {
            this.add(VocabFields.CODE_SYSTEM_ID_NAME);
            this.add(VocabFields.CODE_SYSTEM_NAME_NAME);
            this.add(VocabFields.CODE_SYSTEM_VERSION_NAME);
            this.add(VocabFields.CODE_NAME);
            this.add(VocabFields.DISPLAY_NAME_NAME);
            this.add(VocabFields.TTY_NAME);
        }
    };

    private ODocument drugDoc = new ODocument();

    @Override
    protected boolean processLine(OObjectDatabaseTx dbConnection, ODocument doc, Map<String, String> baseFields, int lineIndex, String line) {
        if (!super.processLine(dbConnection, doc, baseFields, lineIndex, line)) {
            return false;
        }

        String[] lineParts = StringUtils.splitPreserveAllTokens(line, LINE_DELIM, 13);

        if (!lineParts[11].equals(RX_NORM_SRC_ABBR)) {
            return true;
        }

        String tty = doc.field(VocabFields.TTY_NAME);
        boolean brandDrug;

        if (!(brandDrug = MEDICATION_CLINICAL_BRAND_DRUG_TTYS.contains(tty)) && !MEDICATION_CLINICAL_GENERAL_DRUG_TTYS.contains(tty)) {
            return true;
        }

        drugDoc.reset();
        drugDoc.setClassName(CrigttValueSetCodeModel.NAME);

        drugDoc
            .field(VocabFields.VALUE_SET_ID_NAME, (!brandDrug ? MEDICATION_CLINICAL_GENERAL_DRUG_VALUE_SET_ID : MEDICATION_CLINICAL_BRAND_DRUG_VALUE_SET_ID));
        drugDoc.field(VocabFields.VALUE_SET_NAME_NAME, (!brandDrug
            ? MEDICATION_CLINICAL_GENERAL_DRUG_VALUE_SET_NAME : MEDICATION_CLINICAL_BRAND_DRUG_VALUE_SET_NAME));

        DRUG_DOC_COPY_FIELD_NAMES.stream().forEach(drugDocCopyFieldName -> drugDoc.field(drugDocCopyFieldName, ((String) doc.field(drugDocCopyFieldName))));

        drugDoc.save();

        return true;
    }
}