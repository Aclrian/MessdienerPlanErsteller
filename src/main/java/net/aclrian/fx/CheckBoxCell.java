package net.aclrian.fx;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import net.aclrian.mpe.utils.Log;

public class CheckBoxCell<S, T> extends TableCell<S, T> {

	protected final CheckBox checkBox;
	private ObservableValue<T> ov;

	public CheckBoxCell(/*TableView<?> table*/) {
		this.checkBox = new CheckBox();
		this.checkBox.setAlignment(Pos.CENTER);
		/*checkBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue){
					requestFocus();
				}
			}
		});*/
		setAlignment(Pos.CENTER);
		setGraphic(checkBox);
		/*checkBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if(newValue && newValue!=oldValue){
					requestFocus();
				}
			}
		});*/
		setOnKeyPressed(event -> {
			if(event.getSource().equals(KeyCode.SPACE)){
				Log.getLogger().info("sado");
				//checkBox.setSelected(!checkBox.isSelected());
				//this.requestFocus();
			}
		});/*
		checkBox.setOnKeyPressed(event -> {
			if(event.getSource().equals(KeyCode.SPACE)){
				System.out.println("llllll");
				checkBox.setSelected(!checkBox.isSelected());
				this.requestFocus();
			}
		});*/
	}

	@Override
	public void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setGraphic(checkBox);
			if (ov instanceof BooleanProperty) {
				checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
			}
			ov = getTableColumn().getCellObservableValue(getIndex());
			if (ov instanceof BooleanProperty) {
				checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
			}
		}
		Platform.runLater(() -> requestFocus());
	}

	@Override
	public void startEdit() {
		super.startEdit();
		if (isEmpty()) {
			return;
		}
		checkBox.setDisable(false);
		checkBox.requestFocus();
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		checkBox.setDisable(true);
	}
}