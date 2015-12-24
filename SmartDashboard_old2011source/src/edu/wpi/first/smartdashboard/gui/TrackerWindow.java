package edu.wpi.first.smartdashboard.gui;

import edu.wpi.first.smartdashboard.StateManager;
import edu.wpi.first.smartdashboard.state.Record;
import edu.wpi.first.smartdashboard.types.Types.Type;
import edu.wpi.first.smartdashboard.util.DisplayElement;
import edu.wpi.first.smartdashboard.util.IStateListener;
import edu.wpi.first.smartdashboard.util.IStateUpdatable;
import edu.wpi.first.smartdashboard.util.StatefulDisplayElement;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.sql.Types;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author team 2084
 */
public class TrackerWindow extends JFrame implements IStateListener
{
    private static TrackerWindow instance = null;
    private TrackerPanel m_trackerPanel;
    private StateManager m_stateMan;
    
    ////// this is a "singleton" -- meaning there should only be one TrackerWindow in existence at a time
    public static void init(StateManager stateMan)
    {
        if(instance == null)
            instance = new TrackerWindow(stateMan);
    }
    
    public TrackerWindow(final StateManager stateMan)
    {
        m_stateMan = stateMan;
        stateMan.registerForAnnouncements(this);
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run()
            {
                m_trackerPanel = new TrackerPanel();
                
                getContentPane().add(m_trackerPanel);
                setMinimumSize(new Dimension(290, 620));
                setResizable(false);
                setVisible(true);
            }
        });
    }
    
    public static TrackerWindow getInstance()
    {
        return instance;
    }

   public IStateUpdatable newField(final Record r)
    {
        try
        {
            List<Class> choices = DisplayElementRegistry.elementsForType(r.getType());
            final StatefulDisplayElement elem = (StatefulDisplayElement)choices.get(0).newInstance();
            elem.setFieldName(r.getName());
            elem.setRecord(r);
            
            if(r.getName().equals("Robot X") && r.getType().getId() == Types.DOUBLE)
            {
                double x = Double.parseDouble((String)r.getValue());
          //      m_trackerPanel.setRobotX(x);
            }
            
            if(r.getName().equals("Robot Y") && r.getType().getId() == Types.DOUBLE)
            {
                double y = Double.parseDouble((String)r.getValue());
            //    m_trackerPanel.setRobotY(y);
            }
            
            return elem;
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
	    EventQueue.invokeLater(new Runnable() {

		public void run() {
		    JOptionPane.showMessageDialog(null,
			    "Something went wrong creating a field whose type was: " + r.getType(),
			    "Unrecognized Type Received",
			    JOptionPane.WARNING_MESSAGE);
		}
	    });
            return null;
        }
    }
}
