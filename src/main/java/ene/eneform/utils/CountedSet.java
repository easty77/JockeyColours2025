/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ene.eneform.utils;

import java.util.*;

/**
 *
 * @author Simon
 */
public class CountedSet{
    
    private WordCounter m_wordCounter = new WordCounter();
    private Collection<String> m_set;

    public CountedSet(Collection<String> set)
    {
        m_set = set;
    }
    public CountedSet(CountedSet cs, Comparator<Pair<String,Integer>> comparator)
    {
        TreeSet<Pair<String, Integer>> setPairValues = new TreeSet<Pair<String, Integer>>(comparator);
        Iterator<String> iter = cs.iterator();
        while(iter.hasNext())
        {
            String strValue = iter.next();
            int nCount = cs.getCount(strValue);
            setPairValues.add(new Pair<String,Integer>(strValue, nCount));
        }
        Collection<String> newSetValues = new ArrayList<String>();
        Iterator<Pair<String, Integer>> iterp = setPairValues.iterator();
        while(iterp.hasNext())
        {
            Pair<String, Integer> pair = iterp.next();
            newSetValues.add(pair.getElement0());
        }
        m_set = newSetValues;
        m_wordCounter = cs.m_wordCounter;
    }
    public boolean addValue(String strValue)
    {
        if ((strValue != null) && !"".equals(strValue))
        {
            m_set.add(strValue.trim());
            m_wordCounter.add(strValue.trim());
            return true;
        }
        else
        {
            System.out.println("Empty or null value");
            return false;
        }
    }    
   public Collection<String> getCollection()
    {
        return m_set;
    }
    public int size()
    {
        return m_set.size();
    }
    public boolean contains(String strEntry)
    {
        return getCollection().contains(strEntry);
    }
    public int getCount(String strValue)
    {
        return m_wordCounter.count(strValue);
    }
    public Iterator<String> iterator()
    {
        return m_set.iterator();
    }
    public Iterator<String> sortedIterator()
    {
//        Set<String> sortedSet = new TreeSet<String>(someComparator);
//        sortedSet.addAll(m_set);
        return iterator();
    }
private class WordCounter
    {
        private HashMap<String, Integer> m_hmWordCounter= new HashMap<String, Integer>();
        public WordCounter()
        {
        }
        public void add(String strWord)
        {
            if (m_hmWordCounter.containsKey(strWord))
            {
                Integer nCount = m_hmWordCounter.get(strWord);
                m_hmWordCounter.put(strWord, ++nCount);
            }
            else
            {
                m_hmWordCounter.put(strWord, 1);
            }
        }
        public int count(String strWord)
        {
            if (m_hmWordCounter.containsKey(strWord))
            {
                return (int) m_hmWordCounter.get(strWord);
            }
            
            return 0;
        }
    }
}
