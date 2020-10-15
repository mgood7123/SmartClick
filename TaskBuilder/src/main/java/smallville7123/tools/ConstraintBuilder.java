package smallville7123.tools;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ConstraintBuilder {
    public static final int parent = -500;

    public Builder withTarget(ConstraintLayout constraintLayout) {
        Builder tmp = new Builder();
        tmp.commandList.TAG = "ConstraintBuilder";
        tmp.withTarget(constraintLayout);
        return tmp;
    }

    public Builder_ withTag(String tag) {
        Builder_ tmp = new Builder_();
        tmp.withTag(tag);
        return tmp;
    }

    public class Builder_ {
        String TAG = "ConstraintBuilder";
        void withTag(String tag) {
            TAG = tag;
        }

        public Builder withTarget(ConstraintLayout constraintLayout) {
            Builder tmp = new Builder();
            tmp.commandList.TAG = TAG;
            tmp.withTarget(constraintLayout);
            return tmp;
        }
    }

}
