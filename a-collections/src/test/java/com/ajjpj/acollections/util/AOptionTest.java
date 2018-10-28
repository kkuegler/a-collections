package com.ajjpj.acollections.util;

import com.ajjpj.acollections.ACollectionOpsTests;
import com.ajjpj.acollections.AIterator;
import com.ajjpj.acollections.immutable.*;
import com.ajjpj.acollections.mutable.AMutableListWrapper;
import com.ajjpj.acollections.mutable.AMutableSetWrapper;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

import static com.ajjpj.acollections.util.AOption.none;
import static com.ajjpj.acollections.util.AOption.some;
import static org.junit.jupiter.api.Assertions.*;


public class AOptionTest implements ACollectionOpsTests {
    @Test void testStaticFactory() {
        assertEquals(none(), AOption.of(null));
        assertEquals(some("a"), AOption.of("a"));
    }

    @Test void testGet() {
        assertThrows(NoSuchElementException.class, () -> none().get());
        assertEquals(1, some(1).get().intValue());
        assertEquals("a", some("a").get());
    }
    @Test void testOrElse() {
        assertEquals("default", none().orElse("default"));
        assertEquals("x", none().orElse("x"));

        assertEquals("a", some("a").orElse("whatever"));
    }
    @Test void testOrElseGet() {
        assertEquals("default", none().orElseGet(() -> "default"));
        assertEquals("a", some("a").orElseGet(() -> {
            throw new RuntimeException("this is never called");
        }));
    }
    @Test void testOrNull() {
        assertNull(none().orNull());
        assertEquals("a", some("a").orNull());
    }
    @Test void testOrElseThrow() {
        assertThrows(Error.class, () -> none().orElseThrow(() -> new Error("my message")), "my message");
        assertEquals("a", some("a").<RuntimeException>orElseThrow(() -> {
            throw new RuntimeException("this is never called");
        }));
    }

    @Test void testToOptional() {
        assertEquals(Optional.empty(), none().toOptional());
        assertEquals(Optional.of("a"), some("a").toOptional());
    }

    @Test void testToString() {
        assertEquals("AOption[]", none().toString());
        assertEquals("AOption[a]", some("a").toString());
    }

    @Override @Test public void testIterator () {
        assertFalse(none().iterator().hasNext());

        final AIterator<String> it = some("a").iterator();
        assertEquals("a", it.next());
        assertFalse(it.hasNext());
    }

    @Override @Test public void testToLinkedList () {
        assertTrue(none().toLinkedList().isEmpty());
        assertEquals(ALinkedList.of("x"), some("x").toLinkedList());
    }
    @Override @Test public void testToVector () {
        assertTrue(none().toVector().isEmpty());
        assertEquals(AVector.of("x"), some("x").toVector());
    }
    @Override @Test public void testToSet () {
        assertTrue(none().toSet().isEmpty());
        assertEquals(AHashSet.of("x"), some("x").toSet());
    }
    @Override @Test public void testToSortedSet () {
        assertTrue(none().toSortedSet().isEmpty());
        assertEquals(ATreeSet.of("x"), some("x").toSortedSet());

        assertEquals(AVector.of(1, 5, 9), some(5).toSortedSet().plus(1).plus(9).toVector());
        assertEquals(AVector.of(9, 5, 1), some(5).toSortedSet(Comparator.<Integer>naturalOrder().reversed()).plus(1).plus(9).toVector());
    }
    @Override @Test public void testToMutableList () {
        assertTrue(none().toMutableList().isEmpty());
        assertEquals(AMutableListWrapper.of(1), some(1).toMutableList());
    }
    @Override @Test public void testToMutableSet () {
        assertTrue(none().toMutableSet().isEmpty());
        assertEquals(AMutableSetWrapper.of("a"), some("a").toMutableSet());
    }

    @Override @Test public void testSize () {
        assertEquals(0, none().size());
        assertEquals(1, some(99).size());
    }
    @Override @Test public void testIsEmpty () {
        assertTrue(none().isEmpty());
        assertFalse(some(1).isEmpty());
    }
    @Override @Test public void testNonEmpty () {
        assertFalse(none().nonEmpty());
        assertTrue(some("123").nonEmpty());
    }
    @Test public void testIsDefined () {
        assertFalse(none().isDefined());
        assertTrue(some("123").isDefined());
    }

    @Override @Test public void testHead () {
        assertThrows(NoSuchElementException.class, () -> none().head());
        assertEquals(1, some(1).head().intValue());
        assertEquals("a", some("a").head());
    }
    @Override @Test public void testHeadOption () {
        assertEquals(none(), none().headOption());
        assertEquals(some("a"), some("a").headOption());
    }

    @Override @Test public void testMap () {
        assertEquals(none(), none().map(x -> "a"));
        assertEquals(some("abc"), some("a").map(x -> x + "bc"));
    }
    @Override @Test public void testFlatMap () {
        assertEquals(none(), none().flatMap(x -> AVector.of(1)));
        assertEquals(none(), none().flatMap(x -> AVector.of(1, 2, 3)));
        assertEquals(none(), none().flatMap(x -> some(23)));

        assertEquals(some(2), some(1).flatMap(x -> some(2*x)));
        assertEquals(some(2), some(1).flatMap(x -> AVector.of(2*x)));
        assertEquals(none(), some(1).flatMap(x -> none()));
        assertEquals(none(), some(1).flatMap(x -> AVector.empty()));
        assertThrows(IllegalArgumentException.class, () -> some(1).flatMap(x -> AVector.of(1, 2)));
    }
    @Override @Test public void testCollect () {
        assertEquals(none(), none().collect(x -> true, Function.identity()));
        assertEquals(none(), none().collect(x -> false, Function.identity()));

        assertEquals(some(1), some(1).collect(x -> x.equals(1), Function.identity()));
        assertEquals(none(), some(1).collect(x -> false, Function.identity()));
        assertEquals(some(2), some(1).collect(x -> true, x -> 2*x));
    }
    @Override @Test public void testCollectFirst () {
        assertEquals(none(), none().collectFirst(x -> true, Function.identity()));
        assertEquals(none(), none().collectFirst(x -> false, Function.identity()));

        assertEquals(some(1), some(1).collectFirst(x -> x.equals(1), Function.identity()));
        assertEquals(none(), some(1).collectFirst(x -> false, Function.identity()));
        assertEquals(some(2), some(1).collectFirst(x -> true, x -> 2*x));
    }

    @Override @Test public void testFilter () {
        assertEquals(none(), none().filter(x -> true));
        assertEquals(none(), none().filter(x -> false));

        assertEquals(some(1), some(1).filter(x -> x.equals(1)));
        assertEquals(none(), some(1).filter(x -> false));
    }

    @Override @Test public void testFilterNot () {
        assertEquals(none(), none().filterNot(x -> true));
        assertEquals(none(), none().filterNot(x -> false));

        assertEquals(none(), some(1).filterNot(x -> x.equals(1)));
        assertEquals(some(1), some(1).filterNot(x -> false));
    }

    @Override @Test public void testFind () {
        assertEquals(none(), none().find(x -> true));
        assertEquals(none(), none().find(x -> false));

        assertEquals(some(1), some(1).find(x -> x.equals(1)));
        assertEquals(none(), some(1).find(x -> ! x.equals(1)));
    }

    @Override @Test public void testForall () {
        assertTrue(none().forall(x -> true));
        assertTrue(none().forall(x -> false));

        assertTrue(some(1).forall(x -> x.equals(1)));
        assertFalse(some(1).forall(x -> !x.equals(1)));
    }

    @Override @Test public void testExists () {
        assertFalse(none().exists(x -> true));
        assertFalse(none().exists(x -> false));

        assertTrue(some(1).exists(x -> x.equals(1)));
        assertFalse(some(1).exists(x -> !x.equals(1)));
    }
    @Override @Test public void testCount () {
        assertEquals(0, none().count(x -> true));
        assertEquals(0, none().count(x -> false));

        assertEquals(1, some(1).count(x -> x.equals(1)));
        assertEquals(0, some(1).count(x -> !x.equals(1)));
    }
    @Override @Test public void testContains () {
        assertFalse(none().contains(1));

        assertTrue(some(1).contains(1));
        assertFalse(some(1).contains(2));
    }

    @Override @Test public void testReduce () {
        assertThrows(NoSuchElementException.class, () -> none().reduce((a, b) -> null));
        assertEquals("a", some("a").reduce((a,b) -> null));
    }
    @Override @Test public void testReduceLeft () {
        assertThrows(NoSuchElementException.class, () -> none().reduceLeft((a, b) -> null));
        assertEquals("a", some("a").reduceLeft((a,b) -> null));
    }
    @Override @Test public void testReduceLeftOption () {
        assertEquals(none(), none().reduceLeftOption((a, b) -> null));
        assertEquals(some("a"), some("a").reduceLeftOption((a,b) -> null));
    }

    @Override @Test public void testGroupBy () {
        assertTrue(none().groupBy(Function.identity()).isEmpty());
        assertEquals(AHashMap.builder().add("a", some("abc")).build(), some("abc").groupBy(s -> s.substring(0, 1)));
    }

    @Override @Test public void testMin () {
        assertThrows(NoSuchElementException.class, () -> none().min());
        assertThrows(NoSuchElementException.class, () -> AOption.<Integer>none().min(Comparator.naturalOrder()));

        assertEquals("a", some("a").min());
        assertEquals("a", some("a").min(Comparator.naturalOrder()));
    }
    @Override @Test public void testMax () {
        assertThrows(NoSuchElementException.class, () -> none().max());
        assertThrows(NoSuchElementException.class, () -> AOption.<Integer>none().max(Comparator.naturalOrder()));

        assertEquals("a", some("a").max());
        assertEquals("a", some("a").max(Comparator.naturalOrder()));
    }

    @Override @Test public void testFold () {
        assertEquals ("a", AOption.<Integer>none().fold("a", (acc, el) -> {
            throw new RuntimeException("this is never called");
        }));

        assertEquals ("a1", AOption.some(1).fold("a", (acc, el) -> acc + el));
    }
    @Override @Test public void testFoldLeft () {
        assertEquals ("a", AOption.<Integer>none().foldLeft("a", (acc, el) -> {
            throw new RuntimeException("this is never called");
        }));

        assertEquals ("a1", AOption.some(1).foldLeft("a", (acc, el) -> acc + el));
    }

    @Override @Test public void testMkString () {
        assertEquals("", none().mkString("|"));
        assertEquals("<>", none().mkString("<", "|", ">"));

        assertEquals("a", some("a").mkString("|"));
        assertEquals("<a>", some("a").mkString("<", "|", ">"));
    }
}
