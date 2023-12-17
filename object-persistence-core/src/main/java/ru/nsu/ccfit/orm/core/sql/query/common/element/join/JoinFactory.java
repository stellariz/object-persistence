package ru.nsu.ccfit.orm.core.sql.query.common.element.join;

public class JoinFactory {

    public static Join createLeftJoin(
            String tableEncoding, String leftJoinColumnEncoding, String rightJoinColumnEncoding
    ) {
        return new Join(JoinType.LEFT_JOIN, tableEncoding, leftJoinColumnEncoding, rightJoinColumnEncoding);
    }

    public static Join createRightJoin(
            String tableEncoding, String leftJoinColumnEncoding, String rightJoinColumnEncoding
    ) {
        return new Join(JoinType.RIGHT_JOIN, tableEncoding, leftJoinColumnEncoding, rightJoinColumnEncoding);
    }

    public static Join createInnerJoin(
            String tableEncoding, String leftJoinColumnEncoding, String rightJoinColumnEncoding
    ) {
        return new Join(JoinType.INNER_JOIN, tableEncoding, leftJoinColumnEncoding, rightJoinColumnEncoding);
    }

}
