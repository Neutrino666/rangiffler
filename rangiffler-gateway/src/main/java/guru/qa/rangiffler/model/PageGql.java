package guru.qa.rangiffler.model;

import graphql.relay.DefaultConnection;
import graphql.relay.DefaultConnectionCursor;
import graphql.relay.DefaultEdge;
import graphql.relay.DefaultPageInfo;
import graphql.relay.Edge;
import graphql.relay.PageInfo;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.NONE)
@ParametersAreNonnullByDefault
public final class PageGql<T> {

  private final List<T> data;
  private final boolean isFirst;
  private final boolean isLast;

  public PageGql(final List<T> data, final boolean isFirst, final boolean isLast) {
    this.data = data;
    this.isFirst = isFirst;
    this.isLast = isLast;
  }

  public DefaultConnection<T> connection() {
    List<Edge<T>> edges = edges();
    return new DefaultConnection<>(
        edges,
        pageInfo(edges)
    );
  }

  private List<Edge<T>> edges() {
    return IntStream.range(0, data.size())
        .mapToObj(idx -> new DefaultEdge<>(
            data.get(idx),
            new DefaultConnectionCursor(String.valueOf(idx))
        ))
        .collect(Collectors.toList());
  }

  private PageInfo pageInfo(List<Edge<T>> edges) {
    return new DefaultPageInfo(
        edges.isEmpty() ? null : edges.getFirst().getCursor(),
        edges.isEmpty() ? null : edges.getLast().getCursor(),
        !isFirst,
        !isLast
    );
  }
}
