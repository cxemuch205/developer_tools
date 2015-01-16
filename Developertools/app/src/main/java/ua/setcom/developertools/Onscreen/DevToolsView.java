package ua.setcom.developertools.Onscreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import ua.setcom.developertools.Classes.Preference;
import ua.setcom.developertools.Helpers.ControlButtons;
import ua.setcom.developertools.Helpers.ControlOperation;
import ua.setcom.developertools.R;

public class DevToolsView {

	private static final String TAG = DevToolsView.class.getSimpleName();

    private static final Preference<DevToolsViewState> viewStatePreference
            = new DevToolsViewState.Preference("onscreen_view_state",
            DevToolsViewState.newDefaultState());

	private View root;
	private View content;
	private View header;
	private Context context;
	private DevToolsViewState state = DevToolsViewState.newDefaultState();
	private DevToolsViewListener viewListener;

	private boolean minimized = false;
	private boolean attached = false;
	private boolean folded = false;
	private boolean initialized = false;
	private boolean hidden = true;


	private DevToolsView() {
	}

	public static DevToolsView newInstance(Context context,
													 DevToolsViewState state,
													 @Nullable DevToolsViewListener viewListener) {
		final DevToolsView result = new DevToolsView();

		result.root = View.inflate(context, R.layout.layout_onscreen, null);
		result.context = context;
		result.viewListener = viewListener;

        final DevToolsViewState persistedState = readState(context);
        if (persistedState != null) {
            result.state = persistedState;
        } else {
            result.state = state;
        }

		return result;
	}

    public static DevToolsViewState readState(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (viewStatePreference.isSet(preferences)) {
            return viewStatePreference.getPreference(preferences);
        } else {
            return null;
        }
    }

	private void setHeight(int height) {
		checkInit();

		final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();

		params.height = height;

		getWindowManager().updateViewLayout(root, params);
	}

	private void init() {

		if (!initialized) {
			final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            for (final ControlButtons buttonWidget : ControlButtons.values()) {
                final View button = root.findViewById(buttonWidget.id);
                if (buttonWidget.tvInfoId != 0) {
                    final TextView tvInfo = (TextView) root.findViewById(buttonWidget.tvInfoId);
                    buttonWidget.tvInfo = tvInfo;
                }
                if (button != null) {
                    button.setVisibility(buttonWidget.isEnable?View.VISIBLE:View.GONE);
                    buttonWidget.object = button;
                    if (button instanceof ToggleButton) {
                        ((ToggleButton) button).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ControlOperation.operation(context, buttonWidget, isChecked);
                            }
                        });
                    } else {
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ControlOperation.operation(context, buttonWidget, null);
                            }
                        });
                    }
                }
            }

			header = root.findViewById(R.id.ll_header);
			content = root.findViewById(R.id.sv_content);

			final View onscreenFoldButton = root.findViewById(R.id.btn_hide);
			onscreenFoldButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (folded) {
						unfold();
					} else {
						fold();
					}
				}
			});

			final View onscreenHideButton = root.findViewById(R.id.btn_minimize_to_action_bar);
			onscreenHideButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					minimize();
				}
			});

			root.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					hide();
				}
			});

			final ImageView onscreenTitleImageView = (ImageView) root.findViewById(R.id.iv_title);
			onscreenTitleImageView.setOnTouchListener(new WindowDragTouchListener(wm, root));

			initialized = true;
		}

	}

	private void checkInit() {
		if (!initialized) {
			throw new IllegalStateException("init() must be called!");
		}
	}

	public void show() {
		if (hidden) {
			init();
			attach();

			hidden = false;
		}
	}

	public void attach() {
		checkInit();

		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		if (!attached) {
			final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					state.getWidth(),
					state.getHeight(),
					state.getX(),
					state.getY(),
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSLUCENT);

			params.gravity = Gravity.TOP | Gravity.LEFT;

			wm.addView(root, params);
			attached = true;
		}
	}

	private void fold() {
		if (!folded) {
			int newHeight = header.getHeight();
			content.setVisibility(View.GONE);
			setHeight(newHeight);
			folded = true;
		}
	}

	private void unfold() {
		if (folded) {
			content.setVisibility(View.VISIBLE);
			setHeight(state.getHeight());
			folded = false;
		}
	}

	public void detach() {
		checkInit();

		if (attached) {
			getWindowManager().removeView(root);
			attached = false;
		}
	}

	public void minimize() {
		checkInit();
		if (!minimized) {

            persistState(context, getCurrentState(!folded));

			detach();

			if (viewListener != null) {
				viewListener.onViewMinimized();
			}

			minimized = true;
		}
	}

    public static void persistState(Context context, DevToolsViewState state) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        viewStatePreference.putPreference(preferences, state);
    }

	public void hide() {
		checkInit();

		if (!hidden) {

            persistState(context, getCurrentState(!folded));

			detach();

			if (viewListener != null) {
				viewListener.onViewHidden();
			}

			hidden = true;
		}
	}

	private WindowManager getWindowManager() {
		return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
	}
	public DevToolsViewState getCurrentState(boolean useRealSize) {
		final WindowManager.LayoutParams params = (WindowManager.LayoutParams) root.getLayoutParams();
		if (useRealSize) {
			return DevToolsViewState.newInstance(params.width, params.height, params.x, params.y);
		} else {
			return DevToolsViewState.newInstance(state.getWidth(), state.getHeight(), params.x, params.y);
		}
	}

	private static class WindowDragTouchListener implements View.OnTouchListener {

		private static final float DIST_EPS = 0f;
		private static final float DIST_MAX = 100000f;
		private static final long TIME_EPS = 0L;

		private final WindowManager wm;
		private int orientation;
		private float x0;
		private float y0;
		private long time = 0;
		private final View view;
		private int displayWidth;
		private int displayHeight;

		public WindowDragTouchListener(WindowManager wm,
									   View view) {
			this.wm = wm;
			this.view = view;
			initDisplayParams();
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (orientation != this.wm.getDefaultDisplay().getOrientation()) {
				// orientation has changed => we need to check display width/height each time window moved
				initDisplayParams();
			}

			//Log.d(TAG, "Action: " + event.getAction());

			final float x1 = event.getRawX();
			final float y1 = event.getRawY();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.d(TAG, "0:" + toString(x0, y0) + ", 1: " + toString(x1, y1));
					x0 = x1;
					y0 = y1;
					return true;

				case MotionEvent.ACTION_MOVE:
					final long currentTime = System.currentTimeMillis();

					if (currentTime - time >= TIME_EPS) {
						time = currentTime;
						processMove(x1, y1);
					}
					return true;
			}

			return false;
		}

		private void initDisplayParams() {
			this.orientation = this.wm.getDefaultDisplay().getOrientation();

			final DisplayMetrics displayMetrics = new DisplayMetrics();
			wm.getDefaultDisplay().getMetrics(displayMetrics);

			this.displayWidth = displayMetrics.widthPixels;
			this.displayHeight = displayMetrics.heightPixels;
		}

		private void processMove(float x1, float y1) {
			final float Δx = x1 - x0;
			final float Δy = y1 - y0;

			final WindowManager.LayoutParams params = (WindowManager.LayoutParams) view.getLayoutParams();
			Log.d(TAG, "0:" + toString(x0, y0) + ", 1: " + toString(x1, y1) + ", Δ: " + toString(Δx, Δy) + ", params: " + toString(params.x, params.y));

			boolean xInBounds = isDistanceInBounds(Δx);
			boolean yInBounds = isDistanceInBounds(Δy);
			if (xInBounds || yInBounds) {

				if (xInBounds) {
					params.x = (int) (params.x + Δx);
				}

				if (yInBounds) {
					params.y = (int) (params.y + Δy);
				}

				params.x = Math.min(Math.max(params.x, 0), displayWidth - params.width);
				params.y = Math.min(Math.max(params.y, 0), displayHeight - params.height);

				wm.updateViewLayout(view, params);

				if (xInBounds) {
					x0 = x1;
				}

				if (yInBounds) {
					y0 = y1;
				}
			}
		}

		private boolean isDistanceInBounds(float δx) {
			δx = Math.abs(δx);
			return δx >= DIST_EPS && δx < DIST_MAX;
		}

		private static String toString(float x, float y) {
			return "(" + formatFloat(x) + ", " + formatFloat(y) + ")";
		}

		private static String formatFloat(float value) {
			if (value >= 0) {
				return "+" + String.format("%.2f", value);
			} else {
				return String.format("%.2f", value);
			}
		}
	}
}
